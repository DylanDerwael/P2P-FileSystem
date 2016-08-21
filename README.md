Distributed P2P filesystem,

Each user makes a vault available for the network, all other users can save parts of there files within the vaults of other users.
each file will be encrypted and split into parts, a merkle tree will be build to check the integrity of the recovered file before merging it back together.

all clients will first notify a discovery server which keeps track of all online users.
