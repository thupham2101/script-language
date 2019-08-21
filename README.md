# Script-language
Scripting language to create database using JavaCC Tree with visitor desgin pattern.

## Some tutorials of JavaCC
http://homepages.gac.edu/~hvidsten/courses/MC270/Labs/project4-GacApplication/project-files/JavaCC/JavaCC-Eclipse.html

https://cs.lmu.edu/~ray/notes/javacc/

https://medium.com/basecs/grammatically-rooting-oneself-with-parse-trees-ec9daeda7dad

https://javacc.org/

## Some initial rules:
**Create object:**
```bash
ADD $n $table
```
Add $n more instances into $table 

**Set attribute for objects:**
```bash
SET [EXACTLY|AT MOST|AT LEAST] $n $table SUCH THAT $attribute = $value
```
Set (exactly | at most | at least) $n instances of $table, such that $attribute = $value.

**Update attribute for objects:**
```bash
UPDATE $n $table $attribute = $value
```
For $n instances of $table that $attribute <> $value, update $attribute = $value. 
