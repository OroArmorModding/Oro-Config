set -euo pipefail
IFS=$'\n\t'

function cleanup {
	    echo "🧹 Cleanup..."
	        rm -f gradle.properties eliorona-sign.asc
	}

trap cleanup SIGINT SIGTERM ERR EXIT

echo "🚀 Preparing to deploy..."

echo "🔑 Decrypting files..."

gpg --quiet --batch --yes --decrypt --passphrase="${GPG_SECRET}" \
	    --output eliorona-sign.asc .build/eliorona-sign.asc.gpg

gpg --quiet --batch --yes --decrypt --passphrase="${GPG_SECRET}" \
	    --output gradle.properties .build/gradle.properties.gpg

gpg --fast-import --no-tty --batch --yes eliorona-sign.asc

echo "📦 Publishing..."

./gradlew uploadArchives -Psign=true

echo "✅ Done!"
