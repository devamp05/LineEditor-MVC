/**
 * Devam Punitbhai Patel
 * dns682
 * 11316715
 */


 repository name: CMPT381-2024-A4 / dns682


             link: https://git.cs.usask.ca/cmpt381-2024-a4/dns682.git


To run the file export all the files from the zip file then run the EditorApp file in src

in IntelliJ run the EditApplication.java file directly or without intelliJ compile all the files using javac and then run EditApplication.java.

All parts of the assignment are complete, working properly and are handling the edge cases when I tested them.

There was an edge case when 2 double numbers where not returning true to == operator even though they looked same when I printed them to the console then I realized it must be that floating point representation problem we were taught in CMPT 215. To solve that I have simply used the absolute difference between to floats and considered it to be 0 if the difference was <= 0.000000001. The code for this is resize in DLine.java.

I have implemented it in a way that interactions are stored on the undo stack once they are complete so, when mouse is released for adjust endpoints and moving and for rotation and scaling as soon as a new interaction happens like a press in ready state or interation involving any of the other key commands other than the one currently used (like rotating or scaling in the different direction also) saves state in the undo stack and can be undone. This can be easily adapted to a system which saves state on some threshold interaction it would just need a few variables and an if condition for each command to check but it is not mentioned as a requirement in the assignment description and I didn't see it happening in the videos either so, I haven't done it but if it were required my system can easily adapt to that change. And I have my unselected lines set to purple instead of dark purple because normal purple seemed to be darker and more visible on my screen. And I have my line handles radius set to 5 which can be changed in the constructor of lineModel class if required.

There were a few other edge cases which gave me trouble to solve and figure out but I was able to solve them eventually and I have mentioned them on my commit messages.

