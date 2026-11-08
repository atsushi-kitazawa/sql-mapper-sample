# Kotlin SQL Mapper Sample

Kotlin で各種 SQL マッパー／データアクセスライブラリ（Spring JDBC, Jdbi 3, jOOQ, MyBatis）のサンプルコードと検証テストを実装したリポジトリです。

すべてのサブプロジェクトで**同一のテーブルスキーマ**および**共通の検証シナリオ**を実装しており、ライブラリごとの書き味や特徴を直接比較できます。

---

## 共通検証シナリオ

1. **CRUD 基本操作**: 単一レコードの作成（自動採番 ID の取得）、ID 検索、更新、削除
2. **1対多 JOIN**: `users` (1) と `orders` (多) の結合・階層オブジェクトマッピング
3. **動的クエリ**: 検索条件（名前の部分一致、メールアドレス等）の有無に応じた動的 SQL 生成
4. **トランザクション管理**: 例外発生時のロールバック動作の検証

---

## 各フレームワークの比較まとめ

| 項目 | Spring JDBC (`JdbcClient`) | Jdbi 3 | jOOQ | MyBatis |
| :--- | :--- | :--- | :--- | :--- |
| **パラダイム** | 関数型 / Fluent API | SqlObject (アノテーション) + Fluent | 型安全 SQL DSL (コード生成) | アノテーション + XML マッパー |
| **型安全性** | 中（実行時 SQL パース） | 中（実行時バインド） | **最高**（コンパイル時にカラム・型チェック） | 中（XML / アノテーション定義） |
| **Kotlin 親和性** | 高（データクラスの自動マッピング） | 高（`KotlinPlugin`, `@BindKotlin`） | **最高**（`jooq-kotlin`, Kotlin 生成コード） | 中〜高（`resultMap` に可変リスト等の注意が必要） |
| **1対多 JOIN** | 手動で `ResultSetExtractor` 実装 | `RowReducer` または `scanResultSet` | **極めて強力**（`multiset()` による型安全マッピング） | **強力**（XML の `<collection>` 定義） |
| **動的クエリ** | 文字列連結 / `StringBuilder` | 文字列構築 + パラメータバインド | **極めて強力**（`Condition` オブジェクトのリスト） | **強力**（XML の `<where>`, `<if>` タグ） |
| **コード自動生成** | なし | なし | **あり**（DDL からコード生成） | オプション（MyBatis Generator 等） |
| **おすすめ用途** | Spring Boot 標準でシンプルに済ませたい場合 | 軽量・非 Spring 環境でモダンな SQL を扱いたい場合 | 複雑なクエリ・型安全性を最重視する大規模エンタープライズ | 既存の SQL アセットや XML による柔軟なクエリ管理が必要な場合 |

---

## 各サブモジュールの実装概要

### 1. Spring JDBC (`:spring-jdbc`)
- **利用バージョン**: Spring Boot 3.4.1 / Spring Framework 6
- **特徴**: Spring 6 で追加された `JdbcClient` を使用。Kotlin のデータクラスと相性がよく、流暢なメソッドチェーンで記述可能。
- **コード例**: [`UserRepository.kt`](file:///Users/atsushi-kitazawa/workspace/kotlin/sql-mapper-sample/spring-jdbc/src/main/kotlin/sample/springjdbc/repository/UserRepository.kt)

### 2. Jdbi 3 (`:jdbi`)
- **利用バージョン**: Jdbi 3.47.0
- **特徴**: `@RegisterKotlinMappers`, `@BindKotlin` を用いた SqlObject インターフェースと、`withHandle` による Fluent スタイルを併用。Spring に依存せず軽量。
- **コード例**: [`UserDao.kt`](file:///Users/atsushi-kitazawa/workspace/kotlin/sql-mapper-sample/jdbi/src/main/kotlin/sample/jdbi/dao/UserDao.kt), [`UserRepository.kt`](file:///Users/atsushi-kitazawa/workspace/kotlin/sql-mapper-sample/jdbi/src/main/kotlin/sample/jdbi/repository/UserRepository.kt)

### 3. jOOQ (`:jooq`)
- **利用バージョン**: jOOQ 3.19.16 (OSS Edition)
- **特徴**: `schema.sql` から `jooq-codegen` (DDLDatabase) により Kotlin コードを自動生成。`multiset()` による 1対多のネストコレクション型安全取得が特長。
- **コード例**: [`UserRepository.kt`](file:///Users/atsushi-kitazawa/workspace/kotlin/sql-mapper-sample/jooq/src/main/kotlin/sample/jooq/repository/UserRepository.kt)

### 4. MyBatis (`:mybatis`)
- **利用バージョン**: MyBatis 3.5.17 / MyBatis-Spring-Boot 3.0.4
- **特徴**: `@Mapper` インターフェースと XML マッパー（`<collection>`, `<if>`）を連携させた王道スタイル。
- **コード例**: [`UserMapper.kt`](file:///Users/atsushi-kitazawa/workspace/kotlin/sql-mapper-sample/mybatis/src/main/kotlin/sample/mybatis/mapper/UserMapper.kt), [`UserMapper.xml`](file:///Users/atsushi-kitazawa/workspace/kotlin/sql-mapper-sample/mybatis/src/main/resources/sample/mybatis/mapper/UserMapper.xml)

---

## 開発環境と実行方法

### 開発環境のセットアップ (Nix)
本プロジェクトは Nix Flakes を利用して JDK 21、Gradle 8、Kotlin CLI を統一しています。

```bash
# Nix devShell を起動
nix develop

# または direnv を利用している場合は自動ロードされます
direnv allow
```

### ビルドとテストの実行

```bash
# 全サブプロジェクトのテストを一括実行
./gradlew test

# 特定のサブプロジェクトのみテスト実行
./gradlew :spring-jdbc:test
./gradlew :jdbi:test
./gradlew :jooq:test
./gradlew :mybatis:test

# jOOQ コード生成の手動実行（schema.sql 変更時）
./gradlew :jooq:generateJooq
```
