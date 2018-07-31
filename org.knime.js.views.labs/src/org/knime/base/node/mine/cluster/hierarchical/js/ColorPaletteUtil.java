/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   Aug 1, 2018 (awalter): created
 */
package org.knime.base.node.mine.cluster.hierarchical.js;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.knime.js.core.CSSUtils;

/**
 * Utility class for color palettes.
 *
 * @author Alison Walter
 */
public final class ColorPaletteUtil {

    // -- Color palettes taken from ColorManager2NodeDialogPane --

    /** 'Paired' palette, contributed from http://colorbrewer2.org. */
    public static final String[] PALETTE_SET1 = {"#33a02c", "#e31a1c", "#b15928", "#6a3d9a", "#1f78b4", "#ff7f00",
        "#b2df8a", "#fdbf6f", "#fb9a99", "#cab2d6", "#a6cee3", "#ffff99"};

    /** 'Set3' palette, contributed from http://colorbrewer2.org. */
    public static final String[] PALETTE_SET2 = {"#fb8072", "#bc80bd", "#b3de69", "#80b1d3", "#fdb462", "#8dd3c7",
        "#bebada", "#ffed6f", "#ccebc5", "#d9d9d9", "#fccde5", "#ffffb3"};

    /** Colorblind safe palette, contributed from Color Universal Design, http://jfly.iam.u-tokyo.ac.jp/color/. */
    public static final String[] PALETTE_SET3 = {"#E69F00", "#56B4E9", "#009E73", "#F0E442", "#0072B2", "#D55E00", "#CC79A7"};

    private final static int SPACING = 5;
    private final static int SIDE_LENGTH = 20;

    private ColorPaletteUtil() {
        // prevent instantiation of util class
    }

    // -- Set 1 ---

    /**
     * Creates an {@link Icon} representation of color palette set 1.
     *
     * @return the {@link Icon}
     */
    public static Icon getColorPaletteSet1AsIcon() {
        return new ColorPaletteIcon(PALETTE_SET1);
    }

    /**
     * Creates an {@link Icon} representation of color palette set 1, with squares the size of the given side length and
     * with the specified spacing between squares.
     *
     * @param squareSideLength the side length of each square, which represents a single color in the palette
     * @param spacingBetweenSquares the spacing between each square
     * @return the {@link Icon}
     */
    public static Icon getColorPaletteSet1AsIcon(final int squareSideLength, final int spacingBetweenSquares) {
        return new ColorPaletteIcon(PALETTE_SET1, squareSideLength, spacingBetweenSquares);
    }

    /**
     * Creates a {@link Component} representation of color palette set 1.
     *
     * @return the {@link Component}
     */
    public static Component getColorPaletteSet1AsComponent() {
        return new ColorPaletteComponent(PALETTE_SET1);
    }

    /**
     * Creates a {@link Component} representation of color palette set 1, with squares the size of the given side length
     * and with the specified spacing between squares.
     *
     * @param squareSideLength the side length of each square, which represents a single color in the palette
     * @param spacingBetweenSquares the spacing between each square
     * @return the {@link Component}
     */
    public static Component getColorPaletteSet1AsComponent(final int squareSideLength,
        final int spacingBetweenSquares) {
        return new ColorPaletteComponent(PALETTE_SET1, squareSideLength, spacingBetweenSquares);
    }

    // -- Set 2 --

    /**
     * Creates an {@link Icon} representation of color palette set 2.
     *
     * @return the {@link Icon}
     */
    public static Icon getColorPaletteSet2AsIcon() {
        return new ColorPaletteIcon(PALETTE_SET2);
    }

    /**
     * Creates an {@link Icon} representation of color palette set 2, with squares the size of the given side length and
     * with the specified spacing between squares.
     *
     * @param squareSideLength the side length of each square, which represents a single color in the palette
     * @param spacingBetweenSquares the spacing between each square
     * @return the {@link Icon}
     */
    public static Icon getColorPaletteSet2AsIcon(final int squareSideLength, final int spacingBetweenSquares) {
        return new ColorPaletteIcon(PALETTE_SET2, squareSideLength, spacingBetweenSquares);
    }

    /**
     * Creates a {@link Component} representation of color palette set 2.
     *
     * @return the {@link Component}
     */
    public static Component getColorPaletteSet2AsComponent() {
        return new ColorPaletteComponent(PALETTE_SET2);
    }

    /**
     * Creates a {@link Component} representation of color palette set 2, with squares the size of the given side length
     * and with the specified spacing between squares.
     *
     * @param squareSideLength the side length of each square, which represents a single color in the palette
     * @param spacingBetweenSquares the spacing between each square
     * @return the {@link Component}
     */
    public static Component getColorPaletteSet2AsComponent(final int squareSideLength,
        final int spacingBetweenSquares) {
        return new ColorPaletteComponent(PALETTE_SET2, squareSideLength, spacingBetweenSquares);
    }

    // -- Set 3 --

    /**
     * Creates an {@link Icon} representation of color palette set 3.
     *
     * @return the {@link Icon}
     */
    public static Icon getColorPaletteSet3AsIcon() {
        return new ColorPaletteIcon(PALETTE_SET3);
    }

    /**
     * Creates an {@link Icon} representation of color palette set 3, with squares the size of the given side length and
     * with the specified spacing between squares.
     *
     * @param squareSideLength the side length of each square, which represents a single color in the palette
     * @param spacingBetweenSquares the spacing between each square
     * @return the {@link Icon}
     */
    public static Icon getColorPaletteSet3AsIcon(final int squareSideLength, final int spacingBetweenSquares) {
        return new ColorPaletteIcon(PALETTE_SET3, squareSideLength, spacingBetweenSquares);
    }

    /**
     * Creates a {@link Component} representation of color palette set 3.
     *
     * @return the {@link Component}
     */
    public static Component getColorPaletteSet3AsComponent() {
        return new ColorPaletteComponent(PALETTE_SET3);
    }

    /**
     * Creates a {@link Component} representation of color palette set 3, with squares the size of the given side length
     * and with the specified spacing between squares.
     *
     * @param squareSideLength the side length of each square, which represents a single color in the palette
     * @param spacingBetweenSquares the spacing between each square
     * @return the {@link Component}
     */
    public static Component getColorPaletteSet3AsComponent(final int squareSideLength,
        final int spacingBetweenSquares) {
        return new ColorPaletteComponent(PALETTE_SET3, squareSideLength, spacingBetweenSquares);
    }

    // -- General --

    /**
     * Create an {@link Icon} representation of the given color palette.
     *
     * @param colorPalette an array of hex colors
     * @return the {@link Icon}
     */
    public static Icon getColorPaletteAsIcon(final String[] colorPalette) {
        return new ColorPaletteIcon(colorPalette);
    }

    /**
     * Create an {@link Icon} representation of the given color palette, with squares the size of the given side length and
     * with the specified spacing between squares.
     *
     * @param colorPalette an array of hex colors
     * @param squareSideLength the side length of each square, which represents a single color in the palette
     * @param spacingBetweenSquares the spacing between each square
     * @return the {@link Icon}
     */
    public static Icon getColorPaletteAsIcon(final String[] colorPalette, final int squareSideLength,
        final int spacingBetweenSquares) {
        return new ColorPaletteIcon(colorPalette, squareSideLength, spacingBetweenSquares);
    }

    /**
     * Create a {@link Component} representation of the given color palette.
     *
     * @param colorPalette an array of hex colors
     * @return the {@link Component}
     */
    public static Component getColorPaletteAsComponent(final String[] colorPalette) {
        return new ColorPaletteComponent(colorPalette);
    }

    /**
     * Create a {@link Component} representation of the given color palette, with squares the size of the given side length and
     * with the specified spacing between squares.
     *
     * @param colorPalette an array of hex colors
     * @param squareSideLength the side length of each square, which represents a single color in the palette
     * @param spacingBetweenSquares the spacing between each square
     * @return the {@link Component}
     */
    public static Component getColorPaletteAsComponent(final String[] colorPalette, final int squareSideLength,
        final int spacingBetweenSquares) {
        return new ColorPaletteComponent(colorPalette, squareSideLength, spacingBetweenSquares);
    }

    // -- Helper methods --

    private static int computeWidth(final int numSquares, final int sideLength, final int spacing) {
        return numSquares * (sideLength + spacing);
    }

    private static void paintColorPalette(final Graphics graphics, final int x, final int y, final String[] hexColors,
        final int sideLength, final int spacing) {
        int cx = x;
        for (final String hexColor : hexColors) {
            graphics.setColor(CSSUtils.colorFromCssHexString(hexColor));
            graphics.fillRect(cx, y, sideLength, sideLength);
            cx += (sideLength + spacing);
        }
    }

    // -- Helper classes --

    private final static class ColorPaletteIcon implements Icon {

        private final String[] hexColors;

        private final int spacing;

        private final int sideLength;

        public ColorPaletteIcon(final String[] colors) {
            hexColors = colors;
            spacing = SPACING;
            sideLength = SIDE_LENGTH;
        }

        public ColorPaletteIcon(final String[] colors, final int squareSideLength, final int spaceBetweenSquares) {
            hexColors = colors;
            spacing = spaceBetweenSquares;
            sideLength = squareSideLength;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
            paintColorPalette(g, x, y, hexColors, sideLength, spacing);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getIconWidth() {
            return computeWidth(hexColors.length, sideLength, spacing);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getIconHeight() {
            return sideLength;
        }

    }

    private final static class ColorPaletteComponent extends JComponent {

        private static final long serialVersionUID = 1L;

        private final String[] hexColors;

        private final int sideLength;

        private final int spacing;

        public ColorPaletteComponent(final String[] colors) {
            super();
            hexColors = colors;
            sideLength = SIDE_LENGTH;
            spacing = SPACING;
        }

        public ColorPaletteComponent(final String[] colors, final int squareSideLength,
            final int spaceBetweenSquares) {
            super();
            hexColors = colors;
            sideLength = squareSideLength;
            spacing = spaceBetweenSquares;
        }

        @Override
        public void paintComponent(final Graphics g) {
            paintColorPalette(g, 0, 0, hexColors, sideLength, spacing);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(computeWidth(hexColors.length, sideLength, spacing), SIDE_LENGTH);
        }
    }
}
