# 구현 사례

업무별 처리 과정과 코드 예제입니다. [예제 구성](SAMPLE-NOTES.md)

| 기능 | 코드 |
|---|---|
| 시험 정보·검색 건수·결과 목록 조합 | [ExamStatisticsQuery](../samples/exam-workflow/src/main/java/com/portfolio/exam/cases/ExamStatisticsQuery.java) |
| 위원 수에 따른 연속 문항 구간 배정 | [ContiguousReviewerAssignment](../samples/exam-workflow/src/main/java/com/portfolio/exam/cases/ContiguousReviewerAssignment.java) |
| 출제계획의 분야·유형·문항을 검토계획으로 이관 | [ReviewPlanMigration](../samples/exam-workflow/src/main/java/com/portfolio/exam/cases/ReviewPlanMigration.java) |
| 엑셀 문항번호로 검토 대상 재구성 | [QuestionNumberImport](../samples/exam-workflow/src/main/java/com/portfolio/exam/cases/QuestionNumberImport.java) |

## 1. 시험 정보·검색 건수·결과 목록 조합

시험 결과 통계 화면의 조회 기능을 개발·수정하였습니다. 검색 조건별 전체 건수와 시험 기본정보를 조회하고, 페이지 조건을 적용한 응시자 목록을 함께 반환하였습니다. 목록과 엑셀 다운로드에도 동일한 검색 조건을 사용하였습니다.

### 코드 예제

전체 건수와 목록에 같은 조회 조건을 전달합니다. 결과가 없으면 시험 기본정보와 빈 목록을 반환합니다.

- 입출력: 총 12건·페이지 크기 10·2페이지 → offset 10의 2건. 총 0건 → 시험 정보와 빈 목록.
- 테스트: 건수·목록의 조건 공유, 페이지 위치와 결과가 없을 때 불필요한 목록 조회 생략을 검증합니다.
- [코드](../samples/exam-workflow/src/main/java/com/portfolio/exam/cases/ExamStatisticsQuery.java) · [테스트 코드](../samples/exam-workflow/src/test/java/com/portfolio/exam/cases/ExamStatisticsQueryTest.java)

## 2. 위원 수에 따른 연속 문항 구간 배정

선정위원에게 문항을 자동 배정하는 기능을 개발·수정하였습니다. 문항 수를 위원 수로 나눈 몫을 기본 배정량으로 계산하고, 나머지는 앞 순서 위원에게 한 문항씩 추가하였습니다. 문항 순서를 유지한 연속 구간을 위원별로 배정하였습니다.

### 코드 예제

문항·위원 목록을 받아 몫과 나머지로 연속 구간을 계산합니다. 빈 값과 중복 식별자는 거절합니다.

- 입출력: 문항 7개·위원 3명 → 1~3번 / 4~5번 / 6~7번. 문항 1개·위원 2명 → 첫 위원 1개, 둘째 0개.
- 테스트: 몫·나머지 배분, 문항 순서 보존, 위원이 더 많은 경우와 중복 입력을 검증합니다.
- [코드](../samples/exam-workflow/src/main/java/com/portfolio/exam/cases/ContiguousReviewerAssignment.java) · [테스트 코드](../samples/exam-workflow/src/test/java/com/portfolio/exam/cases/ContiguousReviewerAssignmentTest.java)

## 3. 출제계획의 분야·유형·문항을 검토계획으로 이관

출제계획의 분야·문항 유형·대상 문항을 검토계획으로 이관하는 기능을 개발·수정하였습니다. 기존 계획 연결 여부를 검사한 뒤 관계와 하위 구성을 저장하였습니다. 전체 변경은 트랜잭션으로 처리하고 오류 시 롤백하였습니다.

### 코드 예제

분야·유형·문항을 하나의 구성 객체로 전달합니다. 저장소의 일괄 저장 함수로 계획 연결과 하위 구성을 처리합니다.

- 입출력: 출제계획의 분야 1개·유형 1개·문항 1개 → 검토계획에 세 묶음 연결. 두 번째 연결 → 거절.
- 테스트: 하위 구성 누락 여부, 재연결 차단, 출제계획 조회 실패 시 저장 미호출을 검증합니다.
- [코드](../samples/exam-workflow/src/main/java/com/portfolio/exam/cases/ReviewPlanMigration.java) · [테스트 코드](../samples/exam-workflow/src/test/java/com/portfolio/exam/cases/ReviewPlanMigrationTest.java)

## 4. 엑셀 문항번호로 검토 대상 재구성

엑셀 문항번호를 기준으로 검토 대상을 다시 구성하는 기능을 개발·수정하였습니다. 기존 대상을 삭제하고, 번호별 메타데이터에서 분야를 조회하여 문항을 등록하였습니다. 삭제와 등록을 같은 트랜잭션으로 처리하였습니다.

### 코드 예제

문항번호를 모두 해석한 후 대상을 교체합니다. 빈 목록·중복 번호·존재하지 않는 문항은 저장 전에 거절합니다.

- 입출력: 2번·1번 입력 → 조회한 분야와 입력 순서로 구성. 두 번째 번호가 없음 → 기존 문항 목록 유지.
- 테스트: 문항번호와 분야의 연결, 입력 순서, 뒤쪽 행의 오류에서 기존 구성 보존을 검증합니다.
- [코드](../samples/exam-workflow/src/main/java/com/portfolio/exam/cases/QuestionNumberImport.java) · [테스트 코드](../samples/exam-workflow/src/test/java/com/portfolio/exam/cases/QuestionNumberImportTest.java)

[수험표 출력 데이터](REPORT-PRESENTATION.md)
