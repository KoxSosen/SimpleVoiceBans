package com.github.koxsosen.common.abstraction;

public class Constants {

    private static final String CHANNEL_IDENTIFIER = "simplevbans:main";

    private static final String ERR_SERIALIZE = "Unable to serialize: ";

    private static final String ERR_DESERIALIZE = "Unable to deserialize: ";

    private static final String ERR_STREAM = "Unable to close the stream: ";

    private static final String ERR_PLUGIN_MESSAGE = "Unable to send plugin message: ";

    private static final String ERR_MUTE = "Determining the muted state has completed exceptionally.";

    private static final String MSG_PROXY_REQUIREMENT = "SimpleVoiceBans on the proxy requires LibertyBans to be installed too. \n" +
            "Install LibertyBans on the proxy, as well as SimpleVoiceChat and SimpleVoiceBans on all backends, and the proxy.";

    private static final String MSG_LOADED = "Loaded SimpleVoiceBans.";

    private static final String MSG_BACKEND = "Since this server has LibertyBans installed, disabling proxy support.";

    private static final String MSG_PROXY = "Since this server doesn't have LibertyBans installed, enabling proxy support.";

    private static final String ERR_BACKEND_MISSING = "Since this server is not proxied, nor it has LibertyBans installed, disabling.";

    public static String getChannelIdentifier() {
        return CHANNEL_IDENTIFIER;
    }

    public static String getErrDeserialize() {
        return ERR_DESERIALIZE;
    }

    public static String getErrPluginMessage() {
        return ERR_PLUGIN_MESSAGE;
    }

    public static String getErrSerialize() {
        return ERR_SERIALIZE;
    }

    public static String getErrStream() {
        return ERR_STREAM;
    }

    public static String getMsgLoaded() {
        return MSG_LOADED;
    }

    public static String getMsgProxyRequirement() {
        return MSG_PROXY_REQUIREMENT;
    }

    public static String getErrMute() {
        return ERR_MUTE;
    }

    public static String getMsgBackend() {
        return MSG_BACKEND;
    }

    public static String getMsgProxy() {
        return MSG_PROXY;
    }

    public static String getErrBackendMissing() {
        return ERR_BACKEND_MISSING;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
