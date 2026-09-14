# 시험 준비와 좌석 배정

시험 시작 전 데이터 점검, 시험장·시험실 배정, 수험번호와 좌석 배정 기능을 개발·개선하였습니다. 접수 데이터와 기관별 수용 인원을 확인하고 시험 운영 화면에 배정 정보를 연결하였습니다.

## 코드 예제

ExamReadinessService는 시험 준비의 검사·배정 과정을 구성한 예제입니다. 아래 상태 검사와 동일 요청 처리·변경 이력은 예제에 적용한 규칙입니다.

1. 접수자의 시험 종목과 일정 검사
2. 시험실 정원 확인
3. 좌석 번호·수험번호 중복 검사
4. 같은 자리의 반복 요청 처리
5. 재배정 시 변경 이력 생성

[코드](../samples/exam-workflow/src/main/java/com/portfolio/exam/readiness/ExamReadinessService.java) · [예제 구성](SAMPLE-NOTES.md)
