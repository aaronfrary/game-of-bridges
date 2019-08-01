Bridges
=======

Download the game here: https://github.com/aaronfrary/game-of-bridges/archive/download.zip

---

Bridges is a logic puzzle played on a rectangular grid of any size. Each puzzle
is defined by its configuration of "islands" - grid cells each with an assigned
capacity between 1 and 8 inclusive. A player must draw "bridges" between the
islands such that the following criteria are met:

- Bridges run only perpendicularly and do not cross.
- At most two bridges connect a pair of islands.
- The number of bridges connected to each island exactly matches the number on
  that island.
- Each island can be reached from any other following a path over the bridges.

Implemented in (mostly) pure functional Clojure - side effects are limited to
the draw functions.  Includes a step-by-step puzzle solver that can give a hint
for the next move or solve a whole puzzle.

Setup
-----

Download and unzip bridges.zip, and double-click the "bridges.jar" file
any time to play. Requires Java.

Gameplay
--------

Mouse over islands to highlight your options. Click on a highlighted segment to
add a bridge there.  Press the "h" key for a hint, or the "s" key to
automatically solve the rest of the puzzle.  Press the "Escape" key to exit.

Puzzles
-------

Three sample puzzles are included in the "puzzles" directory in bridges.zip,
and you can easily add more of your own.  Each puzzle is just a text file
containing an ASCII picture of the initial board state:

```
3.4...3
.......
5..5..5
.......
2..1...
.1....3
```

The drawing should be rectangular and can be of any size.  Numbers from 1 to 8
are interpreted as islands, and any other character is interpreted as empty
space.

The game and solver have been tested on boards up to 25x25, containing up to 87
islands, and makes no guarantees about behavior on ill-formed or unsolvable
puzzles (or any puzzle not included with the game, for that matter).

Solver
------

The puzzle solver is based on the intersection and elimination search algorithm
described by Yen et al. in 2011.  Where they implement a computationally
expensive elimination search phase, I instead modified the intersection phase
to take into account the rule that all islands must be connected, and remove
from consideration moves that violate this rule.  This does not necessarily
provide a benefit in terms of speed, but it guarantees that even in difficult
scenarios that require the connection rule to solve, the algorithm will find a
next move that is *required* by the current game state.  In contrast,
elimination search would find a random move that happens to be part of some
solution to the puzzle.  I like to think the removal of brute force search
makes for a much more elegant algorithm `:)`

### Disclaimer

I have made no attempt to systematically test this code on a wide variety of
puzzles.  If you find puzzles that are not handled properly, feel free to
[contact me](http://www.aaronfrary.com) with feedback and/or suggestions.

References
----------

- Conceptis Puzzles.  Hashi techniques.
  [[link](http://www.conceptispuzzles.com/index.aspx?uri=puzzle/hashi/techniques)]

- Yen, Shi-Jim, et al.
  Elimination Search for puzzle games: An Application for Hashi Solver,
  in *IEEE International Conference on Fuzzy Systems (FUZZ)*,
  June 27-30, 2011, Taipei, Taiwan, 185-189. IEEE.
  [[link](http://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=6007662)]

License
-------

Copyright © 2015 Aaron Graham-Horowitz

Distributed under the GNU General Public License version 3.
