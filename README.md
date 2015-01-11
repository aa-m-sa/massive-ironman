massive-ironman
===============

Joulun robokurssi 2014 @ TKTL / HY

Lego Mindstroms robot course, Christmas Break 2014 @ CS / University of Helsinki

[Course webpage.](http://www.cs.helsinki.fi/courses/582326/2014/s/a/1)

##objective

Tic-Tac-Toe bot using Lego Mindstorms NXT, Java (LeJOS), OpenCV and webcam.

![Tic Tac Toe game board grid recognition with Hough transform](http://i.imgur.com/HYCiMfc.jpg)

##project structure

* `docs` required project documentation (plans, weekly reports, the final report), in Finnish
* `TestBots`: a couple of 'testbot' programs (that can uploaded to NXT) for testing that the build environment, uploading software to NXT, Bluetooth connections and so function as intended
* `PenBot`: a robot client that draws 'X' characters on a Tic Tac Toe game board (the dimensions and location of the game board pre-configured); receives draw commands over Bluetooth
* `BotCommander`: a utility test program for sending commands to PenBot over Bluetooth
* `BotGame`: the actual game program, runs on PC and controls the PenBot over BT. Currently can recognize the human moves from webcam feed (though needs some assistance from user to discard worthless pics e.g. human hand between cam and the tic tac toe board) and command the Penbot to draw the AI moves on a paper.

Note: Due to the course time constraints, couldn't work much on the BotGame AI, so it remains fairly stupid.

##building

* requirements:
    * Lego Mindstorms NXT
    * LeJOS 0.9.1 (+ necessary Bluetooth libraries, e.g. libbleutooth-dev on Ubuntu)
    * OpenCV (fresh version that has Java bindings; be sure to install all OpenCV's prerequisite libraries before compiling)

* PenBot: after LeJOS is successfully flashed on the NXT brick and the `NXJ_HOME` environment variable has been set, run `ant` in the correct directory
* BotGame: no ant scripts (sorry), just the Eclipse project. Check that the Eclipse can find the OpenCV jars and shared objects and also the LeJOS PC-API libraries (`pccomm.jar` etc).
