package xyz.wagyourtail.voxelshare.packets;


import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum PacketOpcodes {
    //OX: control
    Error(0x00), PING(0x01), ConfigSync(0x02), World(0x03),

    //1X: player packets
    Player(0x10), Position(0x11), Positions(0x12),

    //2X: region packets
    RegionData(0x20), RequestRegion(0x21), RequestRegions(0x22),
    HaveRegion(0x23), HaveRegions(0x24),

    //3X: waypoint packets
    Waypoint(0x30), Waypoints(0x31), DeleteWaypoint(0x32), EditWaypoint(0x33);


    private static final Map<Byte, PacketOpcodes> opcodes = Arrays.stream(values()).collect(Collectors.toMap((e) -> e.opcode, Function.identity()));
    public final byte opcode;

    PacketOpcodes(int opcode) {
        this.opcode = (byte) opcode;
    }

    public static PacketOpcodes getByOpcode(byte opcode) {
        return opcodes.get(opcode);
    }
}
