ALTER TABLE  Config RENAME to ConfigOld;
CREATE TABLE Config (Id INTEGER PRIMARY KEY AUTOINCREMENT, avoid TEXT, reserved1 TEXT, name TEXT, optimization INTEGER, reserved2 TEXT, reserved3 TEXT, updateTime TEXT, travelmode TEXT, updatelocation INTEGER);
INSERT INTO Config (Id,avoid,reserved1,name,optimization,reserved2,reserved3,updateTime,travelmode,updatelocation) select Id,avoid,reserved1,name,optimization,reserved2,reserved3,updateTime,travelmode,updatelocation from ConfigOld;
DROP TABLE ConfigOld;
ALTER TABLE Config ADD COLUMN reserved4 TEXT;