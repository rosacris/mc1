grammar Poet;

events : event* ;

event : '(' INT ',Event {' evtr ',' pred ',' succ ',' icnf ',' disa ',' alte '})' STMTEND ;

eventList : '[' ( INT ( ',' INT )* )? ']' ;

pred : 'pred = ' eventList ;

succ : 'succ = ' eventList ;

icnf : 'icnf = ' eventList ;

disa : 'disa = ' eventList ;

alte : 'alte = ' '['? eventList ']'?;

evtr : 'evtr = (' proc ',' tr_id ',' acts ')' ;

proc : STRING ;

tr_id : INT ;

acts : '[' ( actName ( ',' actName )* )? ']' ;

actName : ID ('(' ID STRING INT ')')? ;


// Use semicolon as statement end
STMTEND : NEWLINE+;
fragment NEWLINE : '\r' '\n' | '\n' | '\r';

// Match identifiers using C syntax
ID : ID_LETTER ( ID_LETTER | DIGIT )* ;
fragment ID_LETTER : 'a'..'z' | 'A'..'Z' | '_' ;
fragment DIGIT : '0'..'9' ;

// Numbers
INT : DIGIT+ ;

// Match double-quoted strings
STRING : '"' ( ESC | . )*? '"' ;
fragment ESC : '\\' [btnr"\\] ;

// Match whitespace in lexer and remove it.
WS : [ \t\r\n]+ -> skip ;
