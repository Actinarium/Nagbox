CREATE TABLE tasks (
    "_id" INTEGER PRIMARY KEY,
    "title" TEXT NOT NULL,
    "interval" INTEGER NOT NULL,
    "flags" INTEGER NOT NULL,
    "nextFireAt" INTEGER)
