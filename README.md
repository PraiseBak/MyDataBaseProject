
---

# 개요

2020년 2학년 고급 자료구조 과제: B+ 트리 기반 데이터베이스 프로젝트  
개발 기간: 2개월

# 요구사항

- **Wine 데이터에 특화된 B+ 트리 기반 데이터베이스 구현**
- **대량의 wine 데이터를 저장할 때 메모리를 효율적으로 사용할 것**
- **속도 최적화: 성능 저하 없이 빠르게 동작할 것**

# 기능

- **B+Tree 구현**
    - 메모리 절약을 위해 **브랜치 간 연결을 제거하고** 부모를 통해 노드 탐색하도록 설계
    - 파일 기반 저장을 지원하며, 대량 데이터를 처리할 때 메모리 사용을 최소화
    - `RandomAccessFile`을 사용하여 파일 I/O 처리
- **파일 기반 입력/출력**
    - 입력 파일에서 데이터를 읽어 B+ 트리에 저장
    - 데이터 직렬화 후 저장 및 불러오기 기능
- **검색 기능**
    - 특정 데이터 또는 인덱스에 대한 빠른 탐색 제공
- **데이터 삭제**
    - 특정 인덱스 데이터를 효율적으로 삭제
- **직렬화**
    - 데이터베이스 내용을 파일에 저장하고, 나중에 다시 불러오는 기능 제공

# 파일 명세

- **[DbSystem.java](https://github.com/PraiseBak/MyDataBaseProject/blob/master/DbSystem.java)** - 메인 실행 파일로, 데이터베이스 시스템을 관리하는 역할을 함
- **[DbInterface.java](https://github.com/PraiseBak/MyDataBaseProject/blob/master/DbInterface.java)** - CLI 함수들을 정의한 인터페이스
- **[BPlusTree.java](https://github.com/PraiseBak/MyDataBaseProject/blob/master/BPlusTree.java)** - B+ 트리의 주요 기능을 구현한 클래스
- **[BPlusTreeInterface.java](https://github.com/PraiseBak/MyDataBaseProject/blob/master/BPlusTreeInterface.java)** - B+ 트리 동작을 명세한 인터페이스
- **[DoubleLinkedList.java](https://github.com/PraiseBak/MyDataBaseProject/blob/master/DoubleLinkedList.java)** - B+ 트리의 동작에 필요한 양방향 연결 리스트 구현

---

