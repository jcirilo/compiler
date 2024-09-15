{Teste de pilha de Escopo}

program exemplo;
    var a, b: integer;

    procedure p(x: real);
        var b, c: integer;
        begin
        end;

    procedure q;
        var c, d: integer;

        procedure r(y: real);
            var e, f: integer;
            begin
                f := 2;
            end;

    begin
    end;

begin
end.