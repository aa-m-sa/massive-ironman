massive-ironman
===============

Joulun robokurssi 2014 @ TKTL / HY

Lego Mindstroms robot course, Christmas Break 2014 @ CS / University of Helsinki

[Course webpage.](http://www.cs.helsinki.fi/courses/582326/2014/s/a/1)

##objective

Tic-Tac-Toe bot using Lego Mindstorms, Java, OpenCV and webcam.

##project structure

* `docs` required project documentation (plans, weekly reports, etc), in Finnish
* `TestBots`: different 'testbot' programs for testing that motors, pilots, sensors etc function as intended
* `PenBot`: the robot client that draws Tic-Tac-Toe characters
* `BotCommander`: a utility test program for sending commands to PenBot over Bluetooth
* `Game`: the actual game program, runs on PC and controls the PenBot over BT. Currently can recognize the human moves from webcam feed (though needs a little assistance from user to discard worthless pics e.g. human hand between cam and the tic tac toe board) and command the Penbot to draw the AI moves on a paper.
* todo: a more intelligent AI

![Tic Tac Toe grid recognition with Hough transform](http://i.imgur.com/HYCiMfc.jpg)
