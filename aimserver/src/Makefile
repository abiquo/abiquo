# Source files
GEN_SRC := aim_constants.cpp Aim.cpp aim_types.cpp AimServer.cpp virt_monitor.cpp EventsMonitor.cpp RimpUtils.cpp Rimp.cpp VLan.cpp StringUtils.cpp NodeConfig.cpp iniparser/dictionary.c iniparser/iniparser.c
GEN_OBJ := $(patsubst %.cpp,%.o, $(GEN_SRC))

# Include directories
THRIFT_INC := /usr/local/include/thrift
BOOST_INC := /usr/local/include
VIRT_INC := /usr/include/libvirt
CURL_INC := /usr/include/curl
BOOST_INC := /usr/include
INIPARSER_INC := ./iniparser
HIREDIS_INC := /usr/include/hiredis

INC := -I$(THRIFT_INC) -I$(BOOST_INC) -I$(VIRT_INC) -I$(CURL_INC) -I$(INIPARSER_INC) -I$(BOOST_INC) -I$(HIREDIS_INC) -I.

.PHONY: all clean

all: aim

%.o: %.cpp
	        $(CXX) -Wno-write-strings -Wall -Werror $(INC) -c $< -o $@

aim: AimServer.o $(GEN_OBJ)
	        $(CXX) -pthread -L/lib64 -lhiredis -L/usr/local/lib -lvirt -lthrift -L/usr/lib64 -lcurl -L/usr/lib64 -lboost_filesystem  $^ -o $@

clean:
	        $(RM) *.o aim

	        
