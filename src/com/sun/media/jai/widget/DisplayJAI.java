package com.sun.media.jai.widget;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * Shim replacing com.sun.media.jai.widget.DisplayJAI (Java Advanced Imaging).
 * The library uses it only as a JComponent constructed from a BufferedImage,
 * so this renders the image at its natural size.
 */
public class DisplayJAI extends JPanel {

    private static final long serialVersionUID = 1L;
    protected RenderedImage image;

    public DisplayJAI() {
        super();
    }

    public DisplayJAI(RenderedImage image) {
        super();
        set(image);
    }

    public void set(RenderedImage image) {
        this.image = image;
        if (image != null) {
            Dimension d = new Dimension(image.getWidth(), image.getHeight());
            setPreferredSize(d);
            setSize(d);
        }
        revalidate();
        repaint();
    }

    public RenderedImage getImage() {
        return this.image;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.image instanceof BufferedImage) {
            ((Graphics2D) g).drawImage((BufferedImage) this.image, 0, 0, null);
        } else if (this.image != null) {
            ((Graphics2D) g).drawRenderedImage(this.image, new java.awt.geom.AffineTransform());
        }
    }
}
