program CaracteristicasPascal;

var
    x, y, z: integer;
    resultado: real;

procedure SemVariaveisSemParametros;
begin
end;

procedure ComVariaveisSemParametros;
var
    a, b: integer;
begin
    a := 10;
    b := 20;
end;

procedure SemVariaveisComParametros(a: integer; b: real);
begin
end;

procedure ComVariaveisComParametros(a, b: integer);
var
    resultado: integer;
begin
    resultado := a + b;
end;

procedure VariasVariaveisVariosTipos(a, b: integer; c: real; d: boolean);
var
    i: integer;
    soma: real;
begin
    soma := a + b + c;
    if d then
        begin
        end
    else
        begin
        end;
end;

begin
    SemVariaveisSemParametros;

    ComVariaveisSemParametros;

    SemVariaveisComParametros(5, 10.5);

    ComVariaveisComParametros(15, 25);

    VariasVariaveisVariosTipos(2, 3, 4.5, true);

    x := 5;
    y := 10;
    if x < y then
        begin
        end;

    z := 0;
    while z < 5 do
    begin
        z := z + 1;
    end;

    resultado := (x + y) * 2 / (z - 1);

    if (x > 0) and (y > 0) or (z = 5) then
        begin
        end;
end.
