package net.noerlol.neotrans.utils;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;

public class NullPrintStream extends PrintStream {
    /**
     * Creates a new print stream, without automatic line flushing, with the
     * specified OutputStream. Characters written to the stream are converted
     * to bytes using the platform's default character encoding.
     *
     * @param out The output stream to which values and objects will be
     *            printed
     * @see PrintWriter#PrintWriter(OutputStream)
     */
    public NullPrintStream(@NotNull OutputStream out) {
        super(out);
    }


    public static NullPrintStream getNull() {
        return new NullPrintStream(new NullOutputStream());
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
    public NullPrintStream(@NotNull OutputStream out, boolean autoFlush) {
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
    public NullPrintStream(@NotNull OutputStream out, boolean autoFlush, @NotNull String encoding) throws UnsupportedEncodingException {
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
    public NullPrintStream(OutputStream out, boolean autoFlush, Charset charset) {
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
    public NullPrintStream(@NotNull String fileName) throws FileNotFoundException {
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
    public NullPrintStream(@NotNull String fileName, @NotNull String csn) throws FileNotFoundException, UnsupportedEncodingException {
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
    public NullPrintStream(String fileName, Charset charset) throws IOException {
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
    public NullPrintStream(@NotNull File file) throws FileNotFoundException {
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
    public NullPrintStream(@NotNull File file, @NotNull String csn) throws FileNotFoundException, UnsupportedEncodingException {
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
    public NullPrintStream(File file, Charset charset) throws IOException {
        super(file, charset);
    }
}
