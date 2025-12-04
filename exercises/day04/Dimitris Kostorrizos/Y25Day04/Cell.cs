namespace Y25Day04
{
    /// <summary>
    /// Represents a cell in a two dimensions grid
    /// </summary>
    public sealed class Cell
    {
        /// <summary>
        /// The <see cref="char"/> used to identify an empty position
        /// </summary>
        private const char EmptyPosition = '.';

        /// <summary>
        /// The <see cref="char"/> used to identify an filled position
        /// </summary>
        private const char FilledPosition = '@';

        /// <summary>
        /// The field for the <see cref="AdjacentCells"/>
        /// </summary>
        private readonly List<Cell> _adjacentCells = [];

        /// <summary>
        /// The field for the <see cref="Value"/>
        /// </summary>
        private char _value;

        /// <summary>
        /// The coordinates
        /// </summary>
        public Point Coordinates { get; }

        /// <summary>
        /// The value
        /// </summary>
        public char Value => _value;

        /// <summary>
        /// A flag indicating whether the cell is filled
        /// </summary>
        public bool IsFilled => Value == FilledPosition;

        /// <summary>
        /// Contains the adjacent cells
        /// </summary>
        public IEnumerable<Cell> AdjacentCells => _adjacentCells;

        /// <summary>
        /// Creates a new instance of <see cref="Cell"/>
        /// </summary>
        /// <param name="coordinates">The coordinates</param>
        /// <param name="value">The value</param>
        public Cell(Point coordinates, char value) : base()
        {
            if(value != EmptyPosition && value != FilledPosition)
                throw new ArgumentOutOfRangeException(nameof(value), $"The {nameof(value)} for a cell must be either '{EmptyPosition}' or '{FilledPosition}'.");

            Coordinates = coordinates;

            _value = value;
        }

        /// <summary>
        /// <inheritdoc/>
        /// </summary>
        /// <returns></returns>
        public override string ToString() => $"{Value}, {Coordinates}";

        /// <summary>
        /// Adds the <paramref name="cells"/> as adjacent to the current cell
        /// </summary>
        /// <param name="cells">The cells</param>
        public void AddAdjacentCells(IEnumerable<Cell> cells)
            => _adjacentCells.AddRange(cells);

        /// <summary>
        /// Clears the <see cref="Value"/>, by setting it to <see cref="EmptyPosition"/>
        /// </summary>
        public void Clear()
            => _value = EmptyPosition;
    }    
}