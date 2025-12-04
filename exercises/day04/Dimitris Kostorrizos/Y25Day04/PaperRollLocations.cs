namespace Y25Day04
{
    /// <summary>
    /// Represents the locations of the paper rolls
    /// </summary>
    public sealed class PaperRollLocations
    {
        /// <summary>
        /// The cell
        /// </summary>
        private readonly List<Cell> _cells = [];

        /// <summary>
        /// The number of rows
        /// </summary>
        private readonly int _rows;

        /// <summary>
        /// The number of columns
        /// </summary>
        private readonly int _columns;

        /// <summary>
        /// Creates a new instance of <see cref="PaperRollLocations"/>
        /// </summary>
        /// <param name="paperRollLocations">The paper roll locations</param>
        private PaperRollLocations(char[][] paperRollLocations) : base()
        {
            ArgumentNullException.ThrowIfNull(paperRollLocations);

            _rows = paperRollLocations.GetLength(0);

            _columns = paperRollLocations[0].Length;

            foreach (var (row, rowElements) in paperRollLocations.Index())
            {
                foreach (var (column, value) in rowElements.Index())
                {
                    var cell = new Cell(new Point(row, column), value);

                    _cells.Add(cell);
                }
            }

            foreach (var cell in _cells)
            {
                var adjacentPoints = GetAdjacentPositions(cell.Coordinates);

                var adjacentCells = _cells.Where(x => adjacentPoints.Contains(x.Coordinates));

                cell.AddAdjacentCells(adjacentCells);
            }
        }

        /// <summary>
        /// Creates and returns a <see cref="PaperRollLocations"/> from the specified <paramref name="fileContent"/>
        /// </summary>
        /// <param name="fileContent">The file content</param>
        /// <returns></returns>
        public static PaperRollLocations CreateFromFileContent(string[] fileContent)
        {
            ArgumentNullException.ThrowIfNull(fileContent);

            return new([.. fileContent.Select(x => x.ToArray())]);
        }

        /// <summary>
        /// Returns the amount of paper rolls that can be removed
        /// </summary>
        /// <returns></returns>
        public int GetRemovableRollCount()
        {
            var accessibleRolls = GetAccessibleRolls().ToList();

            var removedRolls = 0;

            while (accessibleRolls.Count > 0)
            {
                removedRolls += accessibleRolls.Count;

                foreach (var accessibleRoll in accessibleRolls)
                    accessibleRoll.Clear();

                accessibleRolls = [.. GetAccessibleRolls()];
            }

            return removedRolls;
        }

        /// <summary>
        /// Returns the accessible paper roll cells
        /// </summary>
        /// <returns></returns>
        public IEnumerable<Cell> GetAccessibleRolls() 
            => _cells.Where(cell => cell.IsFilled)
                .Where(cell => cell.AdjacentCells.Count(adjacentCell => adjacentCell.IsFilled) < 4);

        /// <summary>
        /// Returns the adjacent positions for the specified <paramref name="point"/>
        /// </summary>
        /// <param name="point">The point</param>
        /// <returns></returns>
        private List<Point> GetAdjacentPositions(Point point)
        {
            var results = new List<Point>();

            var leftColumn = point.Column - 1;

            var rightColumn = point.Column + 1;

            var aboveRow = point.Row + 1;

            var belowRow = point.Row - 1;

            Span<int> rowSpan = [aboveRow, point.Row, belowRow];

            Span<int> columnSpan = [leftColumn, point.Column, rightColumn];

            foreach (var row in rowSpan)
            {
                foreach (var column in columnSpan)
                {
                    if (point.Column == column && point.Row == row)
                        continue;

                    if (Point.TryCreateForGrid(row, column, _rows, _columns, out var adjacentPoint))
                        results.Add(adjacentPoint.Value);
                }
            }

            return results;
        }
    }
}