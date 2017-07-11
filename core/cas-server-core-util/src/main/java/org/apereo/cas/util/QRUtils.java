package org.apereo.cas.util;

import com.google.common.base.Throwables;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * This is {@link QRUtils}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public final class QRUtils {

    private QRUtils() {
    }

    /**
     * Generate qr code.
     *
     * @param stream the stream
     * @param key    the key
     */
    public static void generateQRCode(final OutputStream stream, final String key) {
        try {
            final Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);
            hintMap.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
            hintMap.put(EncodeHintType.MARGIN, 2);
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            final QRCodeWriter qrCodeWriter = new QRCodeWriter();
            final BitMatrix byteMatrix = qrCodeWriter.encode(key, BarcodeFormat.QR_CODE, 250, 250, hintMap);
            final int width = byteMatrix.getWidth();
            final BufferedImage image = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();

            final Graphics2D graphics = (Graphics2D) image.getGraphics();
            try {
                graphics.setColor(Color.WHITE);
                graphics.fillRect(0, 0, width, width);
                graphics.setColor(Color.BLACK);

                IntStream.range(0, width)
                        .forEach(i -> IntStream.range(0, width)
                                .filter(j -> byteMatrix.get(i, j))
                                .forEach(j -> graphics.fillRect(i, j, 1, 1)));
            } finally {
                graphics.dispose();
            }
            
            ImageIO.write(image, "png", stream);
        } catch (final Exception e) {
            throw Throwables.propagate(e);
        }
    }
}
