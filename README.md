# 온라인 시험·문항 관리 플랫폼

시험 접수, 시험장·좌석 배정, 응시 결과와 문항 출제·검토·선정을 관리하는 시스템입니다. 시험 결과·부정행위 통계, 응시자 조회, 검토계획, 선정위원 배정과 외부 API를 개발하였습니다.

## 담당 업무와 구현

### 시험 결과 통계

시험 정보와 응시자별 결과를 조회하고, 운영자가 결시 상태를 확정하거나 취소하는 기능을 개발하였습니다. 화면에서 수정한 결과를 목록 단위로 저장하고 동일 조건으로 엑셀 자료를 생성하도록 구성하였습니다.

### 부정행위 통계

부정행위 의심 건의 유형별 건수와 상세 목록을 분리해 조회하였습니다. 운영자가 의심 유형과 내용을 수정한 뒤 판정을 확정하거나 다시 취소할 수 있도록 처리하였습니다.

### 응시자 시험 조회

로그인 사용자의 자격 종목과 시험 목록을 조회하고 시험별 접수 정보, 응시 결과, 환불 및 추가 서류 요청을 처리하였습니다.

### 문항 검토계획

검토계획에 분야, 문항 유형, 대상 문항을 연결하였습니다. 출제계획의 분야·유형·문항을 검토계획으로 이관하고, 엑셀 문항번호의 메타데이터를 조회하여 대상을 다시 구성하였습니다. 관련 데이터의 삭제와 저장은 트랜잭션으로 처리하였습니다.

### 선정위원 배정

선정위원에게 문항을 수동·자동·엑셀 방식으로 배정하였습니다. 이미 존재하는 배정은 수정하고 신규 배정은 추가했으며, 자동 배정은 문항 수를 위원 수로 나눈 몫·나머지에 따라 연속 구간으로 분배하였습니다.

### 문항 통계

문항별 사용 이력과 사용 통계를 조회하고 출제·검토·선정 단계에서 사용할 수 있는 관리 화면을 개발하였습니다.

## 기술 구성

| 구분 | 사용 기술 | 적용 영역 |
|---|---|---|
| Backend | Java, Spring MVC, 전자정부표준프레임워크 | 시험·문항 업무 로직과 웹 요청 처리 |
| Data Access | MyBatis | 복합 조회와 통계 쿼리 구성 |
| Database | Oracle, MariaDB/MySQL 계열 | 시험 운영 및 문항 데이터 저장 |
| View | JSP, JavaScript | 관리자와 응시자 화면 |
| Document | Apache POI, JXLS | 통계와 배정 자료 처리 |
| Build | Maven | 의존성 및 배포 산출물 관리 |

## 구현 사례

| 구현 사례 | 공개 코드 |
|---|---|
| 시험 정보·검색 건수·결과 목록 조합 | [ExamStatisticsQuery](samples/exam-workflow/src/main/java/com/portfolio/exam/cases/ExamStatisticsQuery.java) |
| 위원 수에 따른 연속 문항 구간 배정 | [ContiguousReviewerAssignment](samples/exam-workflow/src/main/java/com/portfolio/exam/cases/ContiguousReviewerAssignment.java) |
| 출제계획의 분야·유형·문항을 검토계획으로 이관 | [ReviewPlanMigration](samples/exam-workflow/src/main/java/com/portfolio/exam/cases/ReviewPlanMigration.java) |
| 엑셀 문항번호로 검토 대상 재구성 | [QuestionNumberImport](samples/exam-workflow/src/main/java/com/portfolio/exam/cases/QuestionNumberImport.java) |

[외부 API](docs/EXTERNAL-API.md) · [수험표 출력](docs/REPORT-PRESENTATION.md) · [시험 준비·좌석 배정](docs/EXAM-READINESS.md) · [ClassRoom 부하 시험](docs/PERFORMANCE-TEST.md) · [AIDT 접근성 검토](docs/AIDT-COMPATIBILITY.md)

## 코드 예제

업무 처리 흐름을 별도로 작성한 예제입니다. 회사 운영 코드와 데이터는 포함하지 않습니다. 예제의 구성과 추가 규칙은 [예제 안내](docs/SAMPLE-NOTES.md)에 있습니다.

| 업무 주제 | 공개 예제의 구성 | 코드 |
|---|---|---|
| 시험 진행과 답안 저장 | 상태 전이 검증, 제출 ID 기반 중복 방지 | [ExamSession](samples/exam-workflow/src/main/java/com/portfolio/exam/ExamSession.java) |
| 결시 상태 확정·취소 | 결시 여부와 현재 확정 상태 검증, 변경 이력 생성 | [ResultStatusService](samples/exam-workflow/src/main/java/com/portfolio/exam/result/ResultStatusService.java) |
| 검토계획 저장 | 분야·문항 전체 검증 후 한 번에 교체 | [ReviewPlanService](samples/exam-workflow/src/main/java/com/portfolio/exam/review/ReviewPlanService.java) |
| 선정위원 자동 배정 | 현재 배정 수를 기준으로 균등 분배, 문항 중복 차단 | [ReviewerAssignmentService](samples/exam-workflow/src/main/java/com/portfolio/exam/assignment/ReviewerAssignmentService.java) |
| 시험 준비·좌석 배정 | 필수 시험정보·정원·좌석·수험번호 검증, 재배정 이력 | [ExamReadinessService](samples/exam-workflow/src/main/java/com/portfolio/exam/readiness/ExamReadinessService.java) |

[담당 업무](docs/CONTRIBUTIONS.md) · [구현 상세](docs/CASE-STUDIES.md) · [코드 목록](docs/FEATURE-MATRIX.md) · [예제 실행·테스트](docs/VALIDATION.md)
