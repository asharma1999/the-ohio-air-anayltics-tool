import os
import pandas as pd
from sqlalchemy import create_engine

# 1. Absolute path to your file on macOS
csv_file_path = ("/Users/anirudhsharma/Desktop/STL_to_FRA_and_PIT_to_LHR.csv")

# 2. Database Connection Configuration
DB_USER = ""  # Replace with your RDS master username if different
DB_PASSWORD = ""  # <--- Put your actual RDS password here
DB_HOST = "ohio-air-analytics-prod-mysql.copocmkkwxk1.us-east-1.rds.amazonaws.com"  # <--- Put your full RDS endpoint here
DB_PORT = "3306"
DB_NAME = "ohio_air_analytics"
TABLE_NAME = "test_international_2025"

# 3. Create the database engine connection string
connection_string = (f"mysql+pymysql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}")
engine = create_engine(connection_string)

print("1. Reading CSV file into memory...")
df = pd.read_csv(csv_file_path)
print(f" Loaded {len(df):,} rows and {len(df.columns)} columns.")

print(f"2. Uploading data to RDS table '{TABLE_NAME}' in 1,000-row chunks...")

# 4. Upload straight into MySQL (chunksize keeps memory usage light and fast)
df.to_sql(
    name=TABLE_NAME,
    con=engine,
    if_exists="replace",  # Use 'replace' to create/overwrite table, or 'append' if table already exists
    index=False,
    chunksize=1000,
)

print("3. Upload complete! All rows successfully inserted.")