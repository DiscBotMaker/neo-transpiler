package net.noerlol.neotrans.utils;

import net.noerlol.util.FastArray;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class StoredPrintStream extends PrintStream {
    private final ArrayList<Integer> messagesPrinted = new ArrayList<>();
    public static StoredPrintStream get(OutputStream outputStream) {
        return new StoredPrintStream(outputStream);
    }
    /**
     * Creates a new print stream, without automatic line flushing, with the
     * specified OutputStream. Characters written to the stream are converted
     * to bytes using the platform's default character encoding.
     *
     * @param out The output stream to which values and objects will be
     *            printed
     * @see PrintWriter#PrintWriter(OutputStream)
     */
    public StoredPrintStream(@NotNull OutputStream out) {
        super(out);
    }

    /**
     * Writes the specified byte to this stream.  If the byte is a newline and
     * automatic flushing is enabled then the {@code flush} method will be
     * invoked on the underlying output stream.
     *
     * <p> Note that the byte is written as given; to write a character that
     * will be translated according to the platform's default character
     * encoding, use the {@code print(char)} or {@code println(char)}
     * methods.
     *
     * @param b The byte to be written
     * @see #print(char)
     * @see #println(char)
     */
    @Override
    public void write(int b) {
        messagesPrinted.add(b);
    }

    /**
     * Writes {@code len} bytes from the specified byte array starting at
     * offset {@code off} to this stream.  If automatic flushing is
     * enabled then the {@code flush} method will be invoked on the underlying
     * output stream.
     *
     * <p> Note that the bytes will be written as given; to write characters
     * that will be translated according to the platform's default character
     * encoding, use the {@code print(char)} or {@code println(char)}
     * methods.
     *
     * @param buf A byte array
     * @param off Offset from which to start taking bytes
     * @param len Number of bytes to write
     */
    @Override
    public void write(@NotNull byte[] buf, int off, int len) {
        this.writeBytes(buf);
    }

    /**
     * Writes all bytes from the specified byte array to this stream. If
     * automatic flushing is enabled then the {@code flush} method will be
     * invoked on the underlying output stream.
     *
     * <p> Note that the bytes will be written as given; to write characters
     * that will be translated according to the platform's default character
     * encoding, use the {@code print(char[])} or {@code println(char[])}
     * methods.
     *
     * @param buf A byte array
     * @throws IOException If an I/O error occurs.
     * @apiNote Although declared to throw {@code IOException}, this method never
     * actually does so. Instead, like other methods that this class
     * overrides, it sets an internal flag which may be tested via the
     * {@link #checkError()} method. To write an array of bytes without having
     * to write a {@code catch} block for the {@code IOException}, use either
     * {@link #writeBytes(byte[] buf) writeBytes(buf)} or
     * {@link #write(byte[], int, int) write(buf, 0, buf.length)}.
     * @implSpec This method is equivalent to
     * {@link PrintStream#write(byte[], int, int)
     * this.write(buf, 0, buf.length)}.
     * @see #writeBytes(byte[])
     * @see #write(byte[], int, int)
     * @since 14
     */
    @Override
    public void write(byte[] buf) throws IOException {
        for (int i = 0; i < buf.length; i++) {
            write(buf[i]);
        }
    }

    /**
     * Writes all bytes from the specified byte array to this stream.
     * If automatic flushing is enabled then the {@code flush} method
     * will be invoked.
     *
     * <p> Note that the bytes will be written as given; to write characters
     * that will be translated according to the platform's default character
     * encoding, use the {@code print(char[])} or {@code println(char[])}
     * methods.
     *
     * @param buf A byte array
     * @implSpec This method is equivalent to
     * {@link #write(byte[], int, int) this.write(buf, 0, buf.length)}.
     * @since 14
     */
    @Override
    public void writeBytes(byte[] buf) {
        for (int i = 0; i < buf.length; i++) {
            write(buf[i]);
        }
    }

    /**
     * Creates a new print stream, with the specified OutputStream and line
     * flushing. Characters written to the stream are converted to bytes using
     * the platform's default character encoding.
     *
     * @param out       The output stream to which values and objects will be
     *                  printed
     * @param autoFlush Whether the output buffer will be flushed
     *                  whenever a byte array is written, one of the
     *                  {@code println} methods is invoked, or a newline
     *                  character or byte ({@code '\n'}) is written
     * @see PrintWriter#PrintWriter(OutputStream, boolean)
     */
    public StoredPrintStream(@NotNull OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    /**
     * Creates a new print stream, with the specified OutputStream, line
     * flushing, and character encoding.
     *
     * @param out       The output stream to which values and objects will be
     *                  printed
     * @param autoFlush Whether the output buffer will be flushed
     *                  whenever a byte array is written, one of the
     *                  {@code println} methods is invoked, or a newline
     *                  character or byte ({@code '\n'}) is written
     * @param encoding  The name of a supported
     *                  <a href="../lang/package-summary.html#charenc">
     *                  character encoding</a>
     * @throws UnsupportedEncodingException If the named encoding is not supported
     * @since 1.4
     */
    public StoredPrintStream(@NotNull OutputStream out, boolean autoFlush, @NotNull String encoding) throws UnsupportedEncodingException {
        super(out, autoFlush, encoding);
    }

    /**
     * Creates a new print stream, with the specified OutputStream, line
     * flushing and charset.  This convenience constructor creates the necessary
     * intermediate {@link OutputStreamWriter OutputStreamWriter},
     * which will encode characters using the provided charset.
     *
     * @param out       The output stream to which values and objects will be
     *                  printed
     * @param autoFlush Whether the output buffer will be flushed
     *                  whenever a byte array is written, one of the
     *                  {@code println} methods is invoked, or a newline
     *                  character or byte ({@code '\n'}) is written
     * @param charset   A {@linkplain Charset charset}
     * @since 10
     */
    public StoredPrintStream(OutputStream out, boolean autoFlush, Charset charset) {
        super(out, autoFlush, charset);
    }

    /**
     * Creates a new print stream, without automatic line flushing, with the
     * specified file name.  This convenience constructor creates
     * the necessary intermediate {@link OutputStreamWriter
     * OutputStreamWriter}, which will encode characters using the
     * {@linkplain Charset#defaultCharset() default charset}
     * for this instance of the Java virtual machine.
     *
     * @param fileName The name of the file to use as the destination of this print
     *                 stream.  If the file exists, then it will be truncated to
     *                 zero size; otherwise, a new file will be created.  The output
     *                 will be written to the file and is buffered.
     * @throws FileNotFoundException If the given file object does not denote an existing, writable
     *                               regular file and a new regular file of that name cannot be
     *                               created, or if some other error occurs while opening or
     *                               creating the file
     * @throws SecurityException     If a security manager is present and {@link
     *                               SecurityManager#checkWrite checkWrite(fileName)} denies write
     *                               access to the file
     * @since 1.5
     */
    public StoredPrintStream(@NotNull String fileName) throws FileNotFoundException {
        super(fileName);
    }

    /**
     * Creates a new print stream, without automatic line flushing, with the
     * specified file name and charset.  This convenience constructor creates
     * the necessary intermediate {@link OutputStreamWriter
     * OutputStreamWriter}, which will encode characters using the provided
     * charset.
     *
     * @param fileName The name of the file to use as the destination of this print
     *                 stream.  If the file exists, then it will be truncated to
     *                 zero size; otherwise, a new file will be created.  The output
     *                 will be written to the file and is buffered.
     * @param csn      The name of a supported {@linkplain Charset
     *                 charset}
     * @throws FileNotFoundException        If the given file object does not denote an existing, writable
     *                                      regular file and a new regular file of that name cannot be
     *                                      created, or if some other error occurs while opening or
     *                                      creating the file
     * @throws SecurityException            If a security manager is present and {@link
     *                                      SecurityManager#checkWrite checkWrite(fileName)} denies write
     *                                      access to the file
     * @throws UnsupportedEncodingException If the named charset is not supported
     * @since 1.5
     */
    public StoredPrintStream(@NotNull String fileName, @NotNull String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(fileName, csn);
    }

    /**
     * Creates a new print stream, without automatic line flushing, with the
     * specified file name and charset.  This convenience constructor creates
     * the necessary intermediate {@link OutputStreamWriter
     * OutputStreamWriter}, which will encode characters using the provided
     * charset.
     *
     * @param fileName The name of the file to use as the destination of this print
     *                 stream.  If the file exists, then it will be truncated to
     *                 zero size; otherwise, a new file will be created.  The output
     *                 will be written to the file and is buffered.
     * @param charset  A {@linkplain Charset charset}
     * @throws IOException       if an I/O error occurs while opening or creating the file
     * @throws SecurityException If a security manager is present and {@link
     *                           SecurityManager#checkWrite checkWrite(fileName)} denies write
     *                           access to the file
     * @since 10
     */
    public StoredPrintStream(String fileName, Charset charset) throws IOException {
        super(fileName, charset);
    }

    /**
     * Creates a new print stream, without automatic line flushing, with the
     * specified file.  This convenience constructor creates the necessary
     * intermediate {@link OutputStreamWriter OutputStreamWriter},
     * which will encode characters using the {@linkplain
     * Charset#defaultCharset() default charset} for this
     * instance of the Java virtual machine.
     *
     * @param file The file to use as the destination of this print stream.  If the
     *             file exists, then it will be truncated to zero size; otherwise,
     *             a new file will be created.  The output will be written to the
     *             file and is buffered.
     * @throws FileNotFoundException If the given file object does not denote an existing, writable
     *                               regular file and a new regular file of that name cannot be
     *                               created, or if some other error occurs while opening or
     *                               creating the file
     * @throws SecurityException     If a security manager is present and {@link
     *                               SecurityManager#checkWrite checkWrite(file.getPath())}
     *                               denies write access to the file
     * @since 1.5
     */
    public StoredPrintStream(@NotNull File file) throws FileNotFoundException {
        super(file);
    }

    /**
     * Creates a new print stream, without automatic line flushing, with the
     * specified file and charset.  This convenience constructor creates
     * the necessary intermediate {@link OutputStreamWriter
     * OutputStreamWriter}, which will encode characters using the provided
     * charset.
     *
     * @param file The file to use as the destination of this print stream.  If the
     *             file exists, then it will be truncated to zero size; otherwise,
     *             a new file will be created.  The output will be written to the
     *             file and is buffered.
     * @param csn  The name of a supported {@linkplain Charset
     *             charset}
     * @throws FileNotFoundException        If the given file object does not denote an existing, writable
     *                                      regular file and a new regular file of that name cannot be
     *                                      created, or if some other error occurs while opening or
     *                                      creating the file
     * @throws SecurityException            If a security manager is present and {@link
     *                                      SecurityManager#checkWrite checkWrite(file.getPath())}
     *                                      denies write access to the file
     * @throws UnsupportedEncodingException If the named charset is not supported
     * @since 1.5
     */
    public StoredPrintStream(@NotNull File file, @NotNull String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(file, csn);
    }

    /**
     * Creates a new print stream, without automatic line flushing, with the
     * specified file and charset.  This convenience constructor creates
     * the necessary intermediate {@link OutputStreamWriter
     * OutputStreamWriter}, which will encode characters using the provided
     * charset.
     *
     * @param file    The file to use as the destination of this print stream.  If the
     *                file exists, then it will be truncated to zero size; otherwise,
     *                a new file will be created.  The output will be written to the
     *                file and is buffered.
     * @param charset A {@linkplain Charset charset}
     * @throws IOException       if an I/O error occurs while opening or creating the file
     * @throws SecurityException If a security manager is present and {@link
     *                           SecurityManager#checkWrite checkWrite(file.getPath())}
     *                           denies write access to the file
     * @since 10
     */
    public StoredPrintStream(File file, Charset charset) throws IOException {
        super(file, charset);
    }

    public ArrayList<Integer> getMessagesPrinted() {
        return this.messagesPrinted;
    }
}
