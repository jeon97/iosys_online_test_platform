# 기능별 구현 근거

| 담당 영역 | 개발한 기능 | 구현 방식 | 공개 예제 |
|---|---|---|---|
| 시험 결과 | 응시자 결과 조회, 결시 확정·취소 | 현재 확정 상태를 확인하고 변경 이력을 남긴 뒤 일괄 반영 | [ResultStatusService](../samples/exam-workflow/src/main/java/com/portfolio/exam/result/ResultStatusService.java) |
| 부정행위 통계 | 유형별 집계, 상세 내용 수정, 판정 확정·취소 | 집계 조회와 상세 목록을 분리하고 판정 상태 변경을 제한 | [구현 상세](IMPLEMENTATION.md) |
| 응시자 기능 | 본인 시험·접수·결과 조회, 환불·서류 요청 | 로그인 사용자 식별자를 모든 조회 조건에 적용 | [기여 내역](CONTRIBUTIONS.md) |
| 검토계획 | 분야·유형·대상 문항 구성 | 전체 입력 검증 후 기존 구성을 일괄 교체 | [ReviewPlanService](../samples/exam-workflow/src/main/java/com/portfolio/exam/review/ReviewPlanService.java) |
| 선정위원 배정 | 수동·자동·엑셀 배정 | 기존 배정 수를 반영해 최소 배정 위원에게 순차 분배 | [ReviewerAssignmentService](../samples/exam-workflow/src/main/java/com/portfolio/exam/assignment/ReviewerAssignmentService.java) |
| 시험 진행 | 시험 시작·답안 제출·종료 | 상태 전이 검증과 제출 ID 기반 중복 방지 | [ExamSession](../samples/exam-workflow/src/main/java/com/portfolio/exam/ExamSession.java) |
| 시험 준비 | 시험 데이터 점검, 시험실·좌석·수험번호 배정 | 필수값·정원·중복 검증 후 재배정 이력 저장 | [ExamReadinessService](../samples/exam-workflow/src/main/java/com/portfolio/exam/readiness/ExamReadinessService.java) |

