using System.Diagnostics.CodeAnalysis;

namespace Y25Day04
{
    /// <summary>
    /// Represents a two dimensional point
    /// </summary>
    public readonly struct Point
    {
        /// <summary>
        /// The row
        /// </summary>
        public int Row { get; }

        /// <summary>
        /// The column
        /// </summary>
        public int Column { get; }

        /// <summary>
        /// Creates a new instance of <see cref="Point"/>
        /// </summary>
        /// <param name="row">The row</param>
        /// <param name="column">The column</param>
        public Point(int row, int column)
        {
            ArgumentOutOfRangeException.ThrowIfNegative(row);

            ArgumentOutOfRangeException.ThrowIfNegative(column);

            Row = row;

            Column = column;
        }

        /// <summary>
        /// <inheritdoc/>
        /// </summary>
        /// <returns></returns>
        public override string ToString() => $"({Row}, {Column})";

        /// <summary>
        /// Tries to create a <see cref="Point"/> from the specified parameters, for a grid of <paramref name="columns"/> and <paramref name="rows"/>
        /// </summary>
        /// <param name="row">The row</param>
        /// <param name="column">The column</param>
        /// <param name="rows">The amount of rows in the grid</param>
        /// <param name="columns">The amount of columns in the grid</param>
        /// <param name="result">The result</param>
        /// <returns></returns>
        public static bool TryCreateForGrid(int row, int column, int rows, int columns, [NotNullWhen(true)] out Point? result)
        {
            result = null;

            if (row < 0)
                return false;

            if (column < 0)
                return false;

            if (row >= rows)
                return false;

            if (column >= columns)
                return false;

            result = new Point(row, column);

            return true;
        }
    }
}