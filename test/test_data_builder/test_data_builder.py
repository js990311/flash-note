import os
import random

class TextDataGenerator:
    def __init__(self, sample_dir="./sample", output_dir="./test_data"):
        self.sample_dir = sample_dir
        self.output_dir = output_dir
        self.tokens = []

        if not os.path.exists(self.output_dir):
            os.makedirs(self.output_dir)

    # 1. ./sample 디렉토리의 파일 다 가져와서 토큰화하는 메서드
    def load_and_tokenize(self):
        if not os.path.exists(self.sample_dir):
            print(f"Error: {self.sample_dir} 디렉토리가 없습니다.")
            return

        files = [f for f in os.listdir(self.sample_dir) if os.path.isfile(os.path.join(self.sample_dir, f))]

        for file_name in files:
            with open(os.path.join(self.sample_dir, file_name), 'r', encoding='utf-8') as f:
                # 공백, 줄바꿈 기준으로 토큰화 (현실적인 단어 뭉치 획득)
                content = f.read()
                self.tokens.extend(content.split())

        print(f"총 {len(files)}개 파일로부터 {len(self.tokens)}개의 토큰을 로드했습니다.")

    # 2. 토큰을 조합해서 60KB 이하의 파일을 10,000개 생성하는 메서드
    def generate_test_files(self, count=10000, target_kb=60):
        if not self.tokens:
            print("사용 가능한 토큰이 없습니다. 먼저 load_and_tokenize를 실행하세요.")
            return

        target_bytes = target_kb * 1024
        print(f"{count}개의 파일 생성 중... (목표 용량: {target_kb}KB)")

        for i in range(1, count + 1):
            current_file_content = []
            current_bytes = 0

            # 용량이 60KB에 도달할 때까지 랜덤 토큰 추가
            while current_bytes < target_bytes:
                token = random.choice(self.tokens) + " "
                token_bytes = len(token.encode('utf-8')) # 한글 3바이트 처리 핵심

                # 목표 용량을 넘지 않도록 체크
                if current_bytes + token_bytes > target_bytes:
                    break

                current_file_content.append(token)
                current_bytes += token_bytes

            # 파일 저장
            file_name = f"test_doc_{i:05d}.txt"
            with open(os.path.join(self.output_dir, file_name), 'w', encoding='utf-8') as f:
                f.write("".join(current_file_content))

            if i % 1000 == 0:
                print(f"{i}번째 파일 생성 완료...")

# --- 실행부 ---
if __name__ == "__main__":
    generator = TextDataGenerator()
    generator.load_and_tokenize()      # 1단계: 토큰화
    generator.generate_test_files()    # 2단계: 1만개 생성