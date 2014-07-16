Bridges
=======

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
the draw functions.

Installation
------------

Builds with [leiningen] (http://leiningen.org). I'll include a download link
once the project is ready for the world.

Upcoming Features
-----------------

- Step-by-step puzzle solver that can give a hint for the next move or solve a
  whole puzzle.

References
----------

- Yen, S. (2011).
  Elimination Search for puzzle games: An Application for Hashi Solver,
  in *IEEE International Conference on Fuzzy Systems (FUZZ)*,
  June 27-30, 2011, Taipei, Taiwan, 185-189. IEEE.
  [[link] (http://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=6007662)]

License
-------

Copyright © 2014 Aaron Graham-Horowitz

Distributed under the GNU General Public License version 3.
