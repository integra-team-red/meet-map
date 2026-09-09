create function distance(
    lat1 double precision,
    lat2 double precision,
    long1 double precision,
    long2 double precision)
returns double precision as $$
begin
    if lat1 is null or long1 is null or lat2 is null or long2 is null then
        return 0;
    end if;
    return sqrt(pow((lat1-lat2), 2) * pow((long1-long2), 2));
end;
$$ language plpgsql;
