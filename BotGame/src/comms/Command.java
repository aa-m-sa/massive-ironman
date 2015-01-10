package comms;

/**
 * Command byte 'protocol' for bluetooth communication with PenBot.
 *
 */
public final class Command {
    /* 0x0? : general messages*/
    public static byte OK = 0x01;
    public static byte QUIT = 0x02;
    /* 0x1? : draw commands*/
    public static byte DRAW = 0x10;

}
