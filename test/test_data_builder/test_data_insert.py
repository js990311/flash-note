import os
import mariadb
from pathlib import Path
from dotenv import load_dotenv

# 1. 환경 변수 설정
current_dir = Path(__file__).resolve().parent
env_path = current_dir / ".." / ".." / ".env"
load_dotenv(dotenv_path=env_path)

def get_db_connection():
    # DATASOURCE_HOST (localhost:3306) 파싱
    raw_host = os.getenv("DATASOURCE_HOST", "localhost:3306")
    if ":" in raw_host:
        host, port = raw_host.split(":")
    else:
        host, port = raw_host, 3306
    try:
        conn = mariadb.connect(
            user=os.getenv("DATASOURCE_USERNAME"),
            password=os.getenv("DATASOURCE_PASSWORD"),
            host=host,
            port=int(port),
            database=os.getenv("DATASOURCE_DB_NAME")
        )
        return conn
    except mariadb.Error as e:
        print(f"MariaDB 연결 실패: {e}")
        return None

def get_or_insert_member(cursor, conn, email, provider, name="TestUser"):
    # 1. 먼저 조회
    sql_select = "SELECT member_id FROM members WHERE email = ? AND provider = ?"
    cursor.execute(sql_select, (email, provider))
    result = cursor.fetchone()

    if result:
        print(f"기존 회원 발견 (ID: {result[0]})")
        return result[0]

    # 2. 없으면 삽입 (RETURNING 사용으로 ID 직접 받기)
    print(f"새 회원 생성 중: {email} ({provider})")
    try:
        sql_insert = "INSERT INTO members (email, provider, name, role) VALUES (?, ?, ?, 'ROLE_USER') RETURNING member_id"
        cursor.execute(sql_insert, (email, provider, name))
        new_id = cursor.fetchone()[0]
        conn.commit()
        print(f"새 회원 생성 완료 (ID: {new_id})")
        return new_id
    except mariadb.Error as e:
        conn.rollback()
        cursor.execute(sql_select, (email, provider))
        result = cursor.fetchone()
        if result:
            return result[0]
        print(f"회원 생성 실패: {e}")
        return None

def bulk_insert_notes(data_dir="./test_data"):
    # 고정된 테스트 계정 정보
    TARGET_EMAIL = "test_perf@example.com"
    TARGET_PROVIDER = "LOCAL"

    conn = get_db_connection()
    if not conn: return
    cursor = conn.cursor()

    # 2. Member ID 확보 (Get or Insert)
    member_id = get_or_insert_member(cursor, conn, TARGET_EMAIL, TARGET_PROVIDER)
    if not member_id:
        print("Member ID 확보 실패로 중단합니다.")
        return

    # 3. 텍스트 파일 로드 및 배치 삽입
    if not os.path.exists(data_dir):
        print("데이터 디렉토리가 없습니다.")
        return

    files = sorted(os.listdir(data_dir))
    chunk_size = 100

    for i in range(0, len(files), chunk_size):
        batch = files[i : i + chunk_size]
        values = []

        for file_name in batch:
            with open(os.path.join(data_dir, file_name), 'r', encoding='utf-8') as f:
                content = f.read()
                # (title, member_id, content)
                values.append((file_name, member_id, content))

        try:
            sql = "INSERT INTO notes (title, member_id, content,published) VALUES (?, ?, ?, true)"
            cursor.executemany(sql, values)
            conn.commit()
            print(f"[{i + len(batch)}/10000] Notes 삽입 완료...")
        except mariadb.Error as e:
            print(f"배치 삽입 오류: {e}")
            conn.rollback()

    cursor.close()
    conn.close()

if __name__ == "__main__":
    bulk_insert_notes()