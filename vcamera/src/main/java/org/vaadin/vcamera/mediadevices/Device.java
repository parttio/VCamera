package org.vaadin.vcamera.mediadevices;

/**
 * A device as returned by Javascripts MediaDevices: enumerateDevices
 */
public class Device {

    public enum DeviceKind {
        videoinput, audioinput, audiooutput
    }

    private String deviceId, label, groupId;
    private DeviceKind kind;

    public DeviceKind getKind() {
        return kind;
    }

    public void setKind(DeviceKind kind) {
        this.kind = kind;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "Device{" +
                "deviceId='" + deviceId + '\'' +
                ", label='" + label + '\'' +
                ", groupId='" + groupId + '\'' +
                ", kind=" + kind +
                '}';
    }
}
