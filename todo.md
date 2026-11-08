# TODO

## 1. 開発環境の準備 (Nix & Toolchain)
- [x] Nix 環境の定義（`flake.nix`）
  - Java 21 (Zulu OpenJDK 21.0.11)
  - Gradle 8 (Gradle 8.14.6)
  - Kotlin CLI (kotlinc 2.4.21)
- [x] `.envrc` (direnv) の設定と動作確認

## 2. プロジェクト基盤の作成
- [x] Gradle プロジェクト構成の確定（マルチプロジェクト構成）
- [x] ルート Gradle 設定ファイルの作成（`settings.gradle.kts`, `build.gradle.kts`）
- [x] バージョンカタログ（`gradle/libs.versions.toml`）によるライブラリバージョン一元管理
- [x] Gradle Wrapper (`gradlew`) の生成 (Gradle 8.14.6)
- [x] サブプロジェクトディレクトリの作成
  - [x] `mybatis`
  - [x] `jooq`
  - [x] `jdbi`
  - [x] `spring-jdbc`

## 3. 共通データベース仕様・テストシナリオの定義
- [x] 使用する RDBMS の選定（H2 Database インメモリ / PostgreSQL 互換モード）
- [x] 共通スキーマ（`schema.sql`）の作成
  - `users` テーブル（id, name, email, created_at）
  - `orders` テーブル（id, user_id, order_code, amount, status, ordered_at）
- [x] テスト用シードデータ（`data.sql`）の作成
- [x] 共通検証シナリオの決定
  - 基本的な CRUD（登録・単一取得・更新・削除）
  - JOIN による複数テーブルの結合取得（User と Orders の 1対多マッピング）
  - 条件指定による動的クエリ（ユーザー名・ステータス等による検索）
  - トランザクション管理（ロールバック検証）

## 4. 各フレームワークの実装
- [x] **Spring JDBC**
  - [x] プロジェクト設定・依存関係の追加（Spring Boot 3.4.1, Spring JDBC, H2）
  - [x] `JdbcClient` を用いた共通シナリオの実装（CRUD, JOIN, 動的クエリ, トランザクション）
  - [x] テストコード（JUnit 5）による動作確認
- [x] **Jdbi (Jdbi 3)**
  - [x] プロジェクト設定・依存関係の追加（`jdbi3-core`, `jdbi3-kotlin`, `jdbi3-kotlin-sqlobject` 等）
  - [x] SqlObject / Fluent API スタイルでの共通シナリオの実装（CRUD, JOIN, 動的クエリ, トランザクション）
  - [x] テストコード（JUnit 5）による動作確認
- [x] **jOOQ**
  - [x] プロジェクト設定・依存関係の追加（jOOQ 3.19.16, jOOQ-Kotlin, H2）
  - [x] スキーマからのコード自動生成設定（jOOQ Codegen + DDLDatabase）
  - [x] Kotlin DSL を用いたクエリおよび共通シナリオの実装（CRUD, multiset による 1対多 JOIN, 動的クエリ, トランザクション）
  - [x] テストコード（JUnit 5）による動作確認
- [x] **MyBatis**
  - [x] プロジェクト設定・依存関係の追加（MyBatis-Spring-Boot-Starter 3.0.4, H2）
  - [x] マッパー（`UserMapper` インターフェース + XML マッパー）での共通シナリオの実装（CRUD, JOIN, 動的クエリ, トランザクション）
  - [x] テストコード（JUnit 5）による動作確認

## 5. まとめ・ドキュメント作成
- [x] 実行方法（Nix devShell 起動、各プロジェクトのビルド・テスト手順）のドキュメント化
- [x] 各 SQL マッパーの書き味・特徴比較（型安全性、ボイラープレート量、Kotlin との親和性など）のまとめ（[README.md](README.md)）
