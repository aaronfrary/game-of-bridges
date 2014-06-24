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

As far as I know this is (will be) the first implementation of the game in
Clojure (please let me know if this is not the case, or if you know someone
who's done it in another Lisp).

Upcoming Features
-----------------

- Complete "basic" game
- Puzzle generation for various sizes and difficulties
- Step-by-step puzzle solver that can be used at any point to hint at the next
  move.

License
-------

Copyright © 2014 Aaron Graham-Horowitz

Distributed under the GNU General Public License version 3.
