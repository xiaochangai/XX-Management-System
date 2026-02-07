import sys
from pathlib import Path

def main():
    if len(sys.argv) < 2:
        print("Usage: python3 scripts/run_sql.py /path/to/sql/file.sql")
        sys.exit(1)

    sql_file = Path(sys.argv[1])
    if not sql_file.exists():
        print(f"SQL file not found: {sql_file}")
        sys.exit(1)

    try:
        import pymysql
    except Exception:
        print("Missing dependency: pymysql. Install with: python3 -m pip install pymysql")
        sys.exit(1)

    sql_text = sql_file.read_text(encoding="utf-8")

    conn = pymysql.connect(
        host="127.0.0.1",
        port=3306,
        user="root",
        password="123456",
        database="smart_admin_v3",
        charset="utf8mb4",
        autocommit=True,
    )

    with conn.cursor() as cursor:
        cursor.execute("SET NAMES utf8mb4")
        for stmt in sql_text.split(";\n"):
            stmt = stmt.strip()
            if not stmt:
                continue
            cursor.execute(stmt)

    conn.close()
    print("SQL execution completed.")

if __name__ == "__main__":
    main()
