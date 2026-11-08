# このプロジェクトについて

* Kotlinで各種SQLマッパーライブラリのサンプルコードを作成します

# 利用するフレームワーク

* Mybatis
* jOOQ
* JDBI
* Spring JDBC
* Hibernate

# ディレクトリ構成

各フレームワークごとにディレクトリを作成し、その配下にサンプルコードを配置します。
各ディレクトリはそれぞれGradleでプロジェクト化されています。

- sql-mapper-sample
  - mybatis
  - jooq
  - jdbi
  - spring-jdbc
  - hibernate

#　構成

構成は下記の通りです。
Nixを利用してこのプロジェクトのみで利用可能な構成とします。

* Kotlin 2.4
* Java 21
* Gradle 8