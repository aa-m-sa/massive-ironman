package comms;

/**
 * Command bytes for bluetooth communications.
 *
 * Quick hack: library of constants for legibility;
 * TODO refactor later to something more elaborate, e.g. an enum structure
 */
public final class Command {
    /* 0x0? : general*/
    public static byte OK = 0x01;
    public static byte QUIT = 0x02;
    /* 0x1? : draw commands*/
    public static byte DRAW = 0x10;

}
