# 구현 사례 상세

담당 기능의 입력, 처리 순서와 예외 경계를 코드·테스트에 연결하였습니다. 원본 구현과 공개 예제의 차이는 각 사례에 명시하였습니다. 기여 확인 범위는 [근거와 공개 예제 구분](SOURCE-SCOPE.md)을 기준으로 합니다.

| 사례 | 구현 주제 | 코드 |
|---|---|---|
| 1 | 시험 정보·검색 건수·결과 목록 조합 | [ExamStatisticsQuery](../samples/exam-workflow/src/main/java/com/portfolio/exam/cases/ExamStatisticsQuery.java) |
| 2 | 위원 수에 따른 연속 문항 구간 배정 | [ContiguousReviewerAssignment](../samples/exam-workflow/src/main/java/com/portfolio/exam/cases/ContiguousReviewerAssignment.java) |
| 3 | 출제계획의 분야·유형·문항을 검토계획으로 이관 | [ReviewPlanMigration](../samples/exam-workflow/src/main/java/com/portfolio/exam/cases/ReviewPlanMigration.java) |
| 4 | 엑셀 문항번호로 검토 대상 재구성 | [QuestionNumberImport](../samples/exam-workflow/src/main/java/com/portfolio/exam/cases/QuestionNumberImport.java) |

## 1. 시험 정보·검색 건수·결과 목록 조합

시험 결과 통계 기능의 개발·수정에 참여하였습니다. 검색 조건에 따른 전체 건수와 시험 기본정보를 조회하고, 페이지 조건을 적용한 응시자 목록을 함께 반환하도록 구성하였습니다. 운영 화면에서 시험 정보와 검색 결과를 함께 표시하는 처리입니다.

**공개 코드**

시험 식별자·상태·페이지 정보를 하나의 조회 조건으로 전달하고 전체 건수와 목록에 같은 조건을 적용하였습니다. 검색 결과가 없어도 시험 기본정보를 반환하고 목록 조회는 생략합니다.

- 입력·결과 예: 총 12건·페이지 크기 10·2페이지 → offset 10의 2건. 총 0건 → 시험 정보와 빈 목록.
- 검증: 건수·목록의 조건 공유, 페이지 위치와 결과가 없을 때 불필요한 목록 조회 생략을 검증하였습니다.
- [구현 코드](../samples/exam-workflow/src/main/java/com/portfolio/exam/cases/ExamStatisticsQuery.java) · [테스트](../samples/exam-workflow/src/test/java/com/portfolio/exam/cases/ExamStatisticsQueryTest.java)

**원본과 구분한 부분**

페이지 크기 상한과 범위를 벗어난 페이지의 빈 목록 처리는 공개 예제의 명시적 규칙입니다. 조회 중 원본 데이터가 변경되는 상황에서 건수와 목록의 스냅샷 일치까지 보장하지 않습니다.

확인 근거: O1 · 시험 결과 Service와 SVN 작업 사본 수정 정보 확인.

## 2. 위원 수에 따른 연속 문항 구간 배정

선정위원 자동 배정 기능의 개발·수정에 참여하였습니다. 문항 목록과 위원 목록을 조회한 뒤 전체 문항 수를 위원 수로 나눈 몫을 기본 배정량으로 계산하였습니다. 나머지는 앞 순서의 위원부터 한 문항씩 더 배정하고, 문항 순서를 유지한 연속 구간을 각 위원에게 연결하였습니다.

**공개 코드**

조회된 두 목록을 입력받아 위원별 문항 묶음을 반환하는 알고리즘으로 재작성하였습니다. 기존 담당 건수를 비교하는 방식과 구별하여 원본의 몫·나머지 배정 방식을 직접 확인할 수 있도록 하였습니다.

- 입력·결과 예: 문항 7개·위원 3명 → 1~3번 / 4~5번 / 6~7번. 문항 1개·위원 2명 → 첫 위원 1개, 둘째 0개.
- 검증: 몫·나머지 배분, 문항 순서 보존, 위원이 더 많은 경우와 중복 입력을 검증하였습니다.
- [구현 코드](../samples/exam-workflow/src/main/java/com/portfolio/exam/cases/ContiguousReviewerAssignment.java) · [테스트](../samples/exam-workflow/src/test/java/com/portfolio/exam/cases/ContiguousReviewerAssignmentTest.java)

**원본과 구분한 부분**

공백·중복 식별자 거절과 배정 위원이 없을 때의 명시적 오류는 예제의 보완입니다. 기존 ReviewerAssignmentService의 최소 배정량 우선 방식은 추가 설계 예제로 유지하였습니다.

확인 근거: O2 · 문항 배정 Service와 SVN 작업 사본 수정 정보 확인.

## 3. 출제계획의 분야·유형·문항을 검토계획으로 이관

검토계획에서 출제계획의 구성을 가져오는 기능의 개발·수정에 참여하였습니다. 이미 연결된 계획이 있는지 확인한 뒤 계획 관계를 갱신하고, 분야·문항 유형·대상 문항을 조회하여 검토계획에 저장하도록 구성하였습니다. 원본은 이 과정을 명시적인 트랜잭션으로 묶고 실패 시 롤백합니다.

**공개 코드**

분야·유형·문항을 하나의 구성 객체로 만들고, 계획 연결과 구성 저장을 저장소의 한 작업으로 표현하였습니다. 같은 검토계획에 다시 연결하려는 요청은 저장 전에 차단합니다.

- 입력·결과 예: 출제계획의 분야 1개·유형 1개·문항 1개 → 검토계획에 세 묶음 연결. 두 번째 연결 → 거절.
- 검증: 하위 구성 누락 여부, 재연결 차단, 원본 계획 조회 실패 시 저장 미호출을 검증하였습니다.
- [구현 코드](../samples/exam-workflow/src/main/java/com/portfolio/exam/cases/ReviewPlanMigration.java) · [테스트](../samples/exam-workflow/src/test/java/com/portfolio/exam/cases/ReviewPlanMigrationTest.java)

**원본과 구분한 부분**

저장소 인터페이스의 일괄 저장 계약은 실제 DB 트랜잭션 구현이 아닙니다. 동시 요청의 중복 연결 방지는 저장소의 제약과 트랜잭션에서 별도로 구현해야 합니다.

확인 근거: O3 · 검토계획 Service의 계획 연결·트랜잭션 처리 확인.

## 4. 엑셀 문항번호로 검토 대상 재구성

문항번호 목록으로 검토 대상 문항을 다시 구성하는 기능의 개발·수정에 참여하였습니다. 원본은 트랜잭션 안에서 기존 대상을 삭제하고, 입력 문항번호의 메타데이터에서 분야를 확인한 뒤 대상 문항을 등록합니다. 중간 오류가 발생하면 삭제와 등록을 함께 롤백합니다.

**공개 코드**

엑셀 파싱 이후의 번호 목록을 받아 문항 메타데이터를 해석하고, 모두 확인된 경우에만 교체하도록 재작성하였습니다. 원본의 업무 흐름을 유지하면서 검증과 쓰기 단계를 분리하였습니다.

- 입력·결과 예: 2번·1번 입력 → 조회한 분야와 입력 순서로 구성. 두 번째 번호가 없음 → 기존 문항 목록 유지.
- 검증: 문항번호와 분야의 연결, 입력 순서, 뒤쪽 행의 오류에서 기존 구성 보존을 검증하였습니다.
- [구현 코드](../samples/exam-workflow/src/main/java/com/portfolio/exam/cases/QuestionNumberImport.java) · [테스트](../samples/exam-workflow/src/test/java/com/portfolio/exam/cases/QuestionNumberImportTest.java)

**원본과 구분한 부분**

전체 사전 검증·빈 입력 거절·중복 문항번호 거절은 공개 예제에서 보완한 규칙입니다. 엑셀 파일 파싱과 실제 DB 롤백을 이 단위 테스트에서 검증한 것은 아닙니다.

확인 근거: O4 · 검토계획의 엑셀 반입·문항 메타데이터 조회 확인.

모든 입력값과 사례 식별자는 설명용으로 구성하였습니다. 단위 테스트 결과는 공개 예제에 한정합니다.

## 접수·출력 추가 사례

수험표의 면제 표시와 사진·약도 선택은 [출력 데이터 구성](REPORT-PRESENTATION.md)에 코드·테스트와 함께 정리하였습니다.
