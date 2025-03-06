package org.readutf.buildstore.api.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class ByteStreamUtils {

    public static void writeInt(ByteArrayOutputStream outputStream, int value) {
        outputStream.write(value >> 24);
        outputStream.write(value >> 16);
        outputStream.write(value >> 8);
        outputStream.write(value);
    }

    public static void writeLong(ByteArrayOutputStream outputStream, long value) {
        for (int i = 0; i < 8; i++) {
            outputStream.write((int) (value >> (8 * (7 - i))));
        }
        outputStream.write((int) value);
    }

    public static long readLong(ByteArrayInputStream inputStream) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            result = (result << 8) | inputStream.read();
        }
        return result;
    }

    public static void writeUUID(ByteArrayOutputStream outputStream, UUID value) {
        writeLong(outputStream, value.getMostSignificantBits());
        writeLong(outputStream, value.getLeastSignificantBits());
    }

    public static UUID readUUID(ByteArrayInputStream inputStream) {
        long mostSignificantBits = readLong(inputStream);
        long leastSignificantBits = readLong(inputStream);
        return new UUID(mostSignificantBits, leastSignificantBits);
    }


    public static int readInt(ByteArrayInputStream inputStream) {
        return (inputStream.read() << 24) | (inputStream.read() << 16) | (inputStream.read() << 8) | inputStream.read();
    }

    public static void writeString(ByteArrayOutputStream outputStream, String value) {
        writeInt(outputStream, value.length());
        for (char c : value.toCharArray()) {
            outputStream.write(c);
        }
    }

    public static String readString(ByteArrayInputStream inputStream) {
        int length = readInt(inputStream);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append((char) inputStream.read());
        }
        return builder.toString();
    }

}
