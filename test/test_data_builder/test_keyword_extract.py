import os
import re
from collections import Counter

def extract_test_keywords(data_dir="./test_data", total_count=30):
    all_words = []

    if not os.path.exists(data_dir):
        print(f"Error: {data_dir} 디렉토리가 존재하지 않습니다.")
        return []

    files = [f for f in os.listdir(data_dir) if f.endswith('.txt')]
    print(f"{len(files)}개의 파일을 분석하여 최적의 부하 테스트 키워드 30개를 추출합니다...")

    # 1. 단어 추출 (한글/영문 2글자 이상만 필터링)
    for file_name in files:
        with open(os.path.join(data_dir, file_name), 'r', encoding='utf-8') as f:
            content = f.read()
            # 한글 2자 이상 또는 영문 2자 이상 추출 (숫자 단독 제외하여 FTS 가용성 확보)
            words = re.findall(r'[가-힣]{2,}|[a-zA-Z]{2,}', content)
            all_words.extend(words)

    # 2. 빈도수 계산
    word_counts = Counter(all_words)
    common_words = word_counts.most_common()
    total_unique = len(word_counts)

    if total_unique < total_count:
        print(f"경고: 고유 단어가 {total_unique}개뿐입니다. 모든 단어를 추출합니다.")
        return [w[0] for w in common_words]

    # 3. 전략적 30개 분배 (고빈도 10 / 중빈도 15 / 저빈도 5)
    # 그룹 1: 고빈도 (상위 10개) - DB에 가장 큰 부하(I/O) 유발
    high_freq = [word for word, count in common_words[:10]]

    # 그룹 2: 중간 빈도 (15개) - 전체 단어 분포의 25% 지점부터 추출하여 변별력 확보
    mid_start = total_unique // 4
    mid_freq = [word for word, count in common_words[mid_start : mid_start + 15]]

    # 그룹 3: 저빈도 (하위 5개) - 인덱스 핀포인트 탐색 성능 확인
    low_freq = [word for word, count in common_words[-5:]]

    # 4. 결과 출력 (리포트용)
    print(f"\n✅ 분석 완료 (총 고유 단어: {total_unique}개)")

    print(f"\n[그룹 1: 고빈도 10개]")
    for word in high_freq:
        print(f"  - {word}: {word_counts[word]}회")

    print(f"\n[그룹 2: 중간 빈도 15개]")
    for word in mid_freq:
        print(f"  - {word}: {word_counts[word]}회")

    print(f"\n[그룹 3: 저빈도 5개]")
    for word in low_freq:
        print(f"  - {word}: {word_counts[word]}회")

    return high_freq + mid_freq + low_freq

if __name__ == "__main__":
    # 30개 추출 실행
    keywords = extract_test_keywords()

    if keywords:
        # 파일 저장
        save_path = "test_keywords.txt"
        with open(save_path, "w", encoding="utf-8") as f:
            for word in keywords:
                f.write(word + "\n")

        print(f"\n🚀 '{save_path}'에 30개의 키워드가 저장되었습니다.")