# KMP Matchplay Dev Container
# Extends the default Codespaces image with Android SDK and Compose dependencies
FROM mcr.microsoft.com/devcontainers/java:17

# Install Android SDK command line tools
USER root
RUN apt-get update && \
    apt-get install -y wget unzip && \
    mkdir -p /usr/local/android-sdk/cmdline-tools && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O /tmp/cmdline-tools.zip && \
    unzip -q /tmp/cmdline-tools.zip -d /usr/local/android-sdk/cmdline-tools && \
    mv /usr/local/android-sdk/cmdline-tools/cmdline-tools /usr/local/android-sdk/cmdline-tools/latest && \
    rm /tmp/cmdline-tools.zip

ENV ANDROID_SDK_ROOT=/usr/local/android-sdk
ENV PATH=$PATH:/usr/local/android-sdk/cmdline-tools/latest/bin

# Accept Android SDK licenses and install build tools/platforms
RUN yes | sdkmanager --sdk_root=${ANDROID_SDK_ROOT} --licenses || true && \
    sdkmanager --sdk_root=${ANDROID_SDK_ROOT} "platform-tools" "platforms;android-36" "build-tools;36.0.0"

USER vscode
