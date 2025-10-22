sudo apt update
sudo apt install -y apt-transport-https ca-certificates dirmngr

# ClickHouse rasmiy repository qo‘shish
echo "deb https://packages.clickhouse.com/deb stable main" | sudo tee /etc/apt/sources.list.d/clickhouse.list

# GPG kalitini qo‘shish
sudo apt-key adv --keyserver keyserver.ubuntu.com --recv 8919F6BD2B48D754

# Yangi repository’ni yuklab olish
sudo apt update

# Server + client’ni o‘rnatish
sudo apt install -y clickhouse-server clickhouse-client

# ClickHouse server xizmatini ishga tushirish
sudo systemctl enable clickhouse-server
sudo systemctl start clickhouse-server
