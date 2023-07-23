
package ch.astorm.jchess.util;

import ch.astorm.jchess.core.Board;
import ch.astorm.jchess.core.Color;
import ch.astorm.jchess.core.Coordinate;
import ch.astorm.jchess.core.Moveable;
import ch.astorm.jchess.core.Position;
import java.io.PrintStream;
import java.util.function.Function;

/**
 * Simple utility class to render a {@code Position}.
 */
public class ASCIIPositionRenderer extends AbstractTextPositionRenderer {
    private static final String[] ROW_HEADER_WHITE = new String[]
    {
        "     ",
        "  _  ",
        " (_) ",
        " (_) ",
        "     ",
        "     ",
        "  __ ",
        "   / ",
        "  /  ",
        "     ",
        "     ",
        "     ",
        "  /  ",
        " (_) ",
        "     ",
        "     ",
        "  _  ",
        " |_  ",
        "  _) ",
        "     ",
        "     ",
        "   . ",
        "  /| ",
        " '-| ",
        "     ",
        "     ",
        "  _  ",
        "  _) ",
        "  _) ",
        "     ",
        "     ",
        "  _  ",
        "   ) ",
        "  /_ ",
        "     ",
        "     ",
        "     ",
        "  /| ",
        "   | ",
        "     "
    };
    
    private static final String[] ROW_HEADER_BLACK = new String[]
    {
        "     ",
        "     ",
        "  /| ",
        "   | ",
        "     ",
        "     ",
        "  _  ",
        "   ) ",
        "  /_ ",
        "     ",
        "     ",
        "  _  ",
        "  _) ",
        "  _) ",
        "     ",
        "     ",
        "   . ",
        "  /| ",
        " '-| ",
        "     ",
        "     ",
        "  _  ",
        " |_  ",
        "  _) ",
        "     ",
        "     ",
        "     ",
        "  /  ",
        " (_) ",
        "     ",
        "     ",
        "  __ ",
        "   / ",
        "  /  ",
        "     ",
        "     ",
        "  _  ",
        " (_) ",
        " (_) ",
        "     "
    };

    private final ASCIIStyle style;
    private Color orientation;

    /**
     * Creates a renderer to the specified {@code out} stream.
     */
    public ASCIIPositionRenderer(PrintStream out, ASCIIStyle style, Color orientation) {
        super(out);
        this.style = style;
        this.orientation = orientation;
    }

    /**
     * Returns the {@code ASCIIStyle} used by this renderer.
     */
    public ASCIIStyle getStyle() {
        return style;
    }
    
    /**
     * Renders the given {@code position} with a {@link DefaultASCIIStyle}.
     */
    public static void render(PrintStream out, Position position, Color orientation) {
        new ASCIIPositionRenderer(out, new DefaultASCIIStyle(), orientation).render(position);
    }
    
    @Override
    public CharSequence renderToString(Position position) {
        Board board = position.getBoard();
        boolean isWhiteOrientation = orientation==Color.WHITE;
        
        char whiteBackgroundStyle = style.getWhiteBackgroundStyle();
        char blackBackgroundStyle = style.getBlackBackgroundStyle();
        char borderStyle = style.getBorderStyle();

        int nbRows = board.getRowsCount();
        int nbColumns = board.getColumnsCount();
        int nbTotalRows = nbRows*ASCIIStyle.NB_ROWS_PER_CELL;
        int nbTotalColumns = nbColumns*ASCIIStyle.NB_COLUMNS_PER_CELL;
        char[][] asciiBoard = new char[nbTotalRows][nbTotalColumns];

        for(int row=0 ; row<nbRows ; ++row) {
            for(int col=0 ; col<nbColumns ; ++col) {
                Coordinate coordinate = new Coordinate(row, col);
                char filling = board.getCellColor(coordinate)==Color.WHITE ? whiteBackgroundStyle : blackBackgroundStyle;
                
                char[][] cell = new char[ASCIIStyle.NB_ROWS_PER_CELL][ASCIIStyle.NB_COLUMNS_PER_CELL];
                for(int ti=0 ; ti<ASCIIStyle.NB_ROWS_PER_CELL ; ++ti) {
                    for(int tc=0 ; tc<ASCIIStyle.NB_COLUMNS_PER_CELL ; ++tc) {
                        cell[ti][tc] = filling;
                    }
                }

                Moveable moveable = position.get(coordinate);
                if(moveable!=null) { style.renderCell(cell, coordinate, moveable); }

                int targetRow = isWhiteOrientation ? row : nbRows - row -1;
                int targetCol = isWhiteOrientation ? col : nbColumns - col -1;
                
                int rowOffset = (nbRows - targetRow - 1)*ASCIIStyle.NB_ROWS_PER_CELL;
                int colOffset = targetCol*ASCIIStyle.NB_COLUMNS_PER_CELL;
                for(int ti=0 ; ti<ASCIIStyle.NB_ROWS_PER_CELL ; ++ti) {
                    for(int tc=0 ; tc<ASCIIStyle.NB_COLUMNS_PER_CELL ; ++tc) {
                        asciiBoard[rowOffset+ti][colOffset+tc] = cell[ti][tc];
                    }
                }
            }
        }

        String lineSeparator = super.getLineSeparator();
        StringBuilder builder = new StringBuilder(256);
        
        //first border
        builder.append("     ");
        printRow(builder, nbTotalColumns+2, i -> borderStyle);
        builder.append(lineSeparator);

        //whole board
        for(int r=0 ; r<nbTotalRows ; ++r) {
            String headerRow = isWhiteOrientation ? ROW_HEADER_WHITE[r] : ROW_HEADER_BLACK[r];
            for(int hr=0 ; hr<headerRow.length() ; ++hr) { builder.append(headerRow.charAt(hr)); }

            builder.append(borderStyle);

            char[] row = asciiBoard[r];
            printRow(builder, nbTotalColumns, colIndex -> row[colIndex]);
            
            builder.append(borderStyle);
            builder.append(lineSeparator);
        }

        //last border
        builder.append("     ");
        printRow(builder, nbTotalColumns+2, i -> borderStyle);
        builder.append(lineSeparator);

        if(isWhiteOrientation) {
            builder.append("                   _        _        _        __       __       _              ").append(lineSeparator);
            builder.append("         /\\       |_)      /        | \\      |_       |_       /        |_|    ").append(lineSeparator);
            builder.append("        /--\\      |_)      \\_       |_/      |__      |        \\_?      | |    ").append(lineSeparator);
            builder.append("                                                                               ").append(lineSeparator);
        } else {
            builder.append("                 _        __        __        _         _        _             ").append(lineSeparator);
            builder.append("       |_|      /        |_        |_        | \\       /        |_)      /\\    ").append(lineSeparator);
            builder.append("       | |      \\_?      |         |__       |_/       \\_       |_)     /--\\   ").append(lineSeparator);
            builder.append("                                                                               ").append(lineSeparator);
        }
        return builder;
    }

    private void printRow(StringBuilder builder, int nbColumns, Function<Integer, Character> content) {
        for(int i=0 ; i<nbColumns ; ++i) {
            builder.append(content.apply(i));
        }
    }
}
