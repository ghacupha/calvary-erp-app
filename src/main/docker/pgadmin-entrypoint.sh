#!/bin/bash

# Wait for PostgreSQL to be ready
until pg_isready -h postgres -U ${PG_DATABASE_PROD_USER}; do
  echo "Waiting for PostgreSQL..."
  sleep 2
done

# Create a server definition file
cat > /pgadmin4/servers.json <<EOF
{
  "Servers": {
    "1": {
      "Name": "PostgreSQL",
      "Group": "Servers",
      "Host": "postgres",
      "Port": 5436,
      "MaintenanceDB": "postgres",
      "Username": "${PG_DATABASE_PROD_USER}",
      "Password": "${PG_DATABASE_PROD_PASSWORD}",
      "SSLMode": "prefer"
    }
  }
}
EOF

#!/bin/bash

# Wait for PostgreSQL to be ready
until pg_isready -h postgres -U ${PG_DATABASE_PROD_USER}; do
  echo "Waiting for PostgreSQL..."
  sleep 2
done

# Create a server definition file
cat > /pgadmin4/servers.json <<EOF
{
  "Servers": {
    "1": {
      "Name": "PostgreSQL",
      "Group": "Servers",
      "Host": "postgres",
      "Port": 5432,
      "MaintenanceDB": "postgres",
      "Username": "${PG_DATABASE_PROD_USER}",
      "Password": "${PG_DATABASE_PROD_PASSWORD}",
      "SSLMode": "prefer"
    }
  }
}
EOF

## Start pgAdmin
#/entrypoint.sh

