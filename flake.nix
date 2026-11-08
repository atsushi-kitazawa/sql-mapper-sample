{
  description = "Kotlin SQL Mapper Sample Project";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
  };

  outputs = { self, nixpkgs }:
    let
      supportedSystems = [
        "aarch64-darwin"
        "x86_64-darwin"
        "aarch64-linux"
        "x86_64-linux"
      ];
      forEachSupportedSystem = f: nixpkgs.lib.genAttrs supportedSystems (system: f {
        pkgs = import nixpkgs {
          inherit system;
          config.allowUnfree = true;
        };
      });
    in
    {
      devShells = forEachSupportedSystem ({ pkgs }:
        let
          jdk = pkgs.jdk21;
          gradle = pkgs.gradle.override { java = jdk; };
        in
        {
          default = pkgs.mkShell {
            packages = [
              jdk
              gradle
              pkgs.kotlin
            ];

            env = {
              JAVA_HOME = "${jdk}";
            };

            shellHook = ''
              export JAVA_HOME="${jdk}"
              echo "=== Kotlin SQL Mapper Sample dev shell ==="
              echo "Java:   $(java -version 2>&1 | head -n 1)"
              echo "Gradle: $(gradle --version 2>&1 | grep '^Gradle ')"
              echo "Kotlin: $(kotlinc -version 2>&1)"
              echo "========================================="
            '';
          };
        }
      );
    };
}
