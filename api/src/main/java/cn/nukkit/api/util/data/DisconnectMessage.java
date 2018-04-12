/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.api.util.data;

public enum DisconnectMessage {
    CANNOT_CONNECT("disconnectionScreen.cantConnect"),
    INVALID_IP("disconnectionScreen.invalidIP"),
    INVALID_NAME("disconnectionScreen.invalidName"),
    INVALID_SKIN("disconnectionScreen.invalidSkin"),
    LOCKED_SKIN("disconnectionScreen.lockedSkin"),
    LOGGED_IN_FROM_OTHER_LOCATION("disconnectionScreen.loggedinOtherLocation"),
    MULTIPLAYER_DISABLED("disconnectionScreen.multiplayerDisabled"),
    NO_REASON("disconnectionScreen.noReason"),
    NO_INTERNET("disconnectionScreen.noInternet"),
    NOT_ALLOWED("disconnectionScreen.notAllowed"),
    NOT_AUTHENTICATED("disconnectionScreen.notAuthenticated"),
    OUTDATED_CLIENT("disconnectionScreen.outdatedClient"),
    OUTDATED_SERVER("disconnectionScreen.outdatedServer"),
    SERVER_FULL("disconnectionScreen.serverFull"),
    NO_LONGER_AVAILABLE_TO_JOIN("disconnectionScreen.sessionNotFound"),
    NO_WIFI("disconnectionScreen.nowifi"),
    INVALID_TENANT("disconnectionScreen.invalidTenant"),
    RESOURCE_PACK("disconnectionScreen.resourcePack"),
    BAD_PACKET("disconnectionScreen.badPacket"),

    CONNECTION_CLOSED("disconnect.closed"),
    RESOURCE_PACK_ERROR("disconnect.downloadPack"),
    DISCONNECTED("disconnect.disconnected"),
    END_OF_STREAM("disconnect.endOfStream"),
    KICKED("disconnect.kicked"),
    LOGIN_FAILED("disconnect.loginFailed"),
    INVALID_SESSION("disconnect.loginFailedInfo.invalidSession"),
    AUTH_SERVERS_DOWN("disconnect.loginFailedInfo.serversUnavailable"),
    CONNECTION_LOST("disconnect.lost"),
    BUFFER_OVERFLOW("disconnect.overflow"),
    QUITTING("disconnect.quitting"),
    SPAM("disconnect.spam"),
    TIMEOUT("disconnect.timeout"),

    REALM_CANNOT_CONNECT("disconnectionScreen.cantConnectToRealm"),
    REALM_INCOMPATIBLE("disconnectionScreen.incompatibleRealm"),
    REALM_ERROR_CANNOT_FIND("disconnectionScreen.internalError.cantFindRealm"),

    REALMS_CANNOT_CONNECT("disconnectionScreen.cantConnectToRealms"),
    REALMS_ALPHA_ENDED("disconnectionScreen.realmsAlphaEndedTitle"),
    REALMS_CLIENT_OUTDATED("disconnectionScreen.outdatedClientRealms"),
    REALMS_SERVER_OUTDATED("disconnectionScreen.outdatedServerRealms"),
    REALMS_SERVER_UPDATE("disconnectionScreen.realmsServerUpdateIncoming"),
    REALMS_DISABLED_BETA("disconnectionScreen.realmsDisabledBeta"),


    EDITION_MISMATCH("disconnectionScreen.editionMismatch"),
    EDITION_MISMATCH_EDU_TO_VANILLA("disconnectionScreen.editionMismatchEduToVanilla"),
    EDITION_MISMATCH_VANILLA_TO_EDU("disconnectionScreen.editionMismatchVanillaToEdu"),

    FUTURE_LEVEL("disconnectionScreen.futureVersion"),

    ERROR_CANNOT_CONNECT("disconnectionScreen.internalError.cantConnect"),
    ERROR_LOADING_WORLD("disconnectionScreen.internalError.cantFindLocal"),
    ERROR_CANNOT_FIND_SERVER("disconnectionScreen.internalError.cantFindServer");

    private final String i18n;

    DisconnectMessage(String i18n) {
        this.i18n = i18n;
    }

    public String i18n() {
        return i18n;
    }
}
