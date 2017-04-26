Lab 4 (demand Paging)

This lab simulated demand paging to test how the number of page faults
depends on page size, program size, replacement algorithm, and job mix

to run: 6 command line arguments are needed-
    M (machine size, int)
    P (page size, int)
    S (size of process, int)
    J (job mix, int)
    N (num references per process, int)
    R (replacement algorithm, "FIFO", "RANDOM", or "LRU")
    

Job mix and calculating the next reference is specified in line
