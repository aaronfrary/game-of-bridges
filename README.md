Bridges
=======

Try the game here: www.aaronfrary.com/game-of-bridges

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

Implemented in ClojureScript, using [re-frame](https://github.com/Day8/re-frame)
for the user interface. Includes a step-by-step puzzle solver that can show a
hint for the next move or solve a whole puzzle.

Gameplay
--------

Mouse over an island to view potential bridges for it. Click on a potential
bridge to create it, or on a single bridge to make it a double bridge.
Click on a double bridge to remove it. Press the "Show Hint" button to
highlight a required move if there is one available, or the "Solve" button
to automatically solve the rest of the puzzle. Choose between three example
puzzles or build your own.

Solver
------

The puzzle solver is based on the intersection and elimination search algorithm
described by Yen et al. in 2011. The elimination search phase is computationally
expensive (involving taking a guess and backtracking if it leads to an error)
and is not needed for most puzzles, so I did not implement it here.  Instead I
modified the intersection phase to take into account the rule that all
islands must be connected, removing from consideration moves that violate this
rule. This covers the primary case that elimination search was needed for in
the original algorithm.

This still leaves some puzzles that the solver cannot complete on its own. In
the minimal example below, there are two valid ways to finish the puzzle: by
adding two vertical bridges, or adding two horizontal bridges.

```
2-2.1
|....
2-3.2
..|.|
..2-2
```

Using only the intersection phase of the algorithm, the solver can only find
moves that are required in all solutions, and so won't make the choice between
the vertical or horizontal solution in this puzzle. I think this is a reasonable
trade-off for making the tool more usable in practice as a puzzle-solving
assistant.

Development
-----------

### Compile css:

Compile css file once.

```
lein less once
```

Automatically recompile css file on change.

```
lein less auto
```

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

### Run tests:

Install test dependencies

```
npm install
npm install -g karma-cli
```

And then run the tests

```
lein clean
lein doo chrome-headless test once
```

Note that [doo](https://github.com/bensu/doo) can be configured to run
cljs.test in many JS environments (phantom, chrome, ie, safari, opera, slimer,
node, rhino, or nashorn), but only headless chrome is included in the dev
dependencies.

Production Build
----------------

To compile clojurescript to javascript:

```
lein clean
lein cljsbuild once min
```

References
----------

- Conceptis Puzzles.  Hashi techniques.
  [[link](http://www.conceptispuzzles.com/index.aspx?uri=puzzle/hashi/techniques)]

- Yen, Shi-Jim, et al.
  Elimination Search for puzzle games: An Application for Hashi Solver,
  in *IEEE International Conference on Fuzzy Systems (FUZZ)*,
  June 27-30, 2011, Taipei, Taiwan, 185-189. IEEE.
  [[link](http://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=6007662)]
