"""
Generates a new hash (sha256) to store inside the file 'plat_sepolicy_and_mapping.sha256' according 
to the combination and order of files

"""
# we will use hashlib
import hashlib
from sys import argv
from os import listdir, path

# policy allow rule line
SEPOLICY_ALLOW_RULE_LINE: str = "(allow apexd sysfs_mmc_host (file (write read getattr)))"


def calcSHA256OfFile(file_name: str) -> str:
    """
    Calculates the SHA256 of a file then returns it in hexadecimal format (as a string)
    """
    # construct a new sha256 object
    # open the file in 'rb' (read binary) mode
    # feed the bytes of the open file to that sha256 object using its method update()
    # finally, request a hexadecimal hash
    # convert that hexadecimal hash into a string for return
    fd_rbin = open(file_name, 'rb')
    sha256obj: hashlib.sha256 = hashlib.sha256()
    sha256obj.update(fd_rbin.read())
    hex_hash = sha256obj.hexdigest()
    return hex_hash

def calcSHA256OfBytesObject(obj: bytes) -> str:
    """
    Calculates the SHA256 of a bytes object (bytes array) then returns it in hexadecimal format (as a string)
    """
    # construct a new sha256 object
    # feed the bytes to that sha256 object using its update() method
    #  finally, request a hexadecimal hash using hexdigest()
    # then return it
    sha256obj: hashlib.sha256 = hashlib.sha256()
    sha256obj.update(obj)
    return sha256obj.hexdigest()

# sys.argv handling
# store current test order in a variable
test_order: int = -1 # uninitialized

CURRENT_DIRECTORY: str = f"{path.dirname(path.abspath(__file__))}"
POLICY_MAPPING_DIR_ABS_PATH: str = f"{CURRENT_DIRECTORY}\\mapping"
PLAT_SEPOLICY_CIL_FILE_ABS_PATH: str = f"{CURRENT_DIRECTORY}\\plat_sepolicy.cil"

# we must know what's the latest mapping file first.
def getLatestPolicyMappingFileAbsPath() -> str:
    """
    Gets the absolute path of the latest policy mapping file and returns it as a string
    """
    global POLICY_MAPPING_DIR_ABS_PATH
    # read mapping/ directory
    # convert each file name's indexes [0:3] into float
    # see which one is the largest value, then return its absolute path.
    __largest: float = 0.0
    for __file in listdir( POLICY_MAPPING_DIR_ABS_PATH ):
        __ver: float = float(__file[0:3])
        if __ver > __largest: __largest = __ver
    
    __latestMappingFileAbsPathStr: str = f"{POLICY_MAPPING_DIR_ABS_PATH}\\{__largest}.cil"
    return __latestMappingFileAbsPathStr
    



# Test orders indicate which files in what order are concatinated to calculate the sha256 hex hash
# Test order 1 : Plat sepolicy.cil + latest mapping file
def generateOrder1HexHash() -> str:
    """
    Generates Order 1 Hexadecimal Hash then returns it as a string

    Order 1 hexadecimal sha256 hash consists of: Plat Sepolicy.cil file + latest policy mapping file contents
    concatinated then calculated their sha256 hexadecimal hash.
    """
    global PLAT_SEPOLICY_CIL_FILE_ABS_PATH
    # open plat sepolicy .cil file in rb
    # open latest policy mapping file in rb
    # concatinate their byte contents together into one object (bytes object/array)
    # construct a new sha256 object
    # feed it these concatinated bytes
    # request a hex sha256 digest
    # return it
    order1bytes: bytes = bytes()
    platfd = open(PLAT_SEPOLICY_CIL_FILE_ABS_PATH, 'rb')
    order1bytes += platfd.read()
    platfd.close()
    mappingfd = open(getLatestPolicyMappingFileAbsPath(), 'rb')
    order1bytes += mappingfd.read()
    mappingfd.close()

    sha256obj: hashlib.sha256 = hashlib.sha256()
    sha256obj.update(order1bytes)
    return sha256obj.hexdigest()
    



# Test order 2 : latest mapping file + Plat sepolicy.cil
def generateOrder2HexHash() -> str:
    """
    Generates Order 2 Hexadecimal Hash then returns it as a string

    Order 2 hexadecimal hash is the hexadecimal hash of the contents of the latest policy mapping file and
    the plat_sepolicy.cil concatinated together.
    """
    global PLAT_SEPOLICY_CIL_FILE_ABS_PATH
    # construct a new bytes object
    # concatinate the bytes of the latest mapping file with the plat sepolicy.cil file
    # construct a new sha256 object 
    # feed it the bytes using its update() method
    # request a new hexdigest() then return it
    order2bytes: bytes = bytes()
    mappingfd = open(getLatestPolicyMappingFileAbsPath(), 'rb')
    order2bytes += mappingfd.read()
    mappingfd.close()
    platfd = open(PLAT_SEPOLICY_CIL_FILE_ABS_PATH, 'rb')
    order2bytes += platfd.read()
    platfd.close()

    sha256obj: hashlib.sha256 = hashlib.sha256()
    sha256obj.update(order2bytes)
    return sha256obj.hexdigest()


# Test order 3 : Plat sepolicy.cil + all mapping files
def generateOrder3HexHash() -> str:
    """
    Generates a hexadecimal sha256 hash of Order 3 then returns it as a string.

    Order 3 hexadecimal sha256 hash consists of the sha256 hexadecimal hash of the contents
    of the plat sepolicy.cil file and all mapping files concatinated.
    """
    global PLAT_SEPOLICY_CIL_FILE_ABS_PATH, POLICY_MAPPING_DIR_ABS_PATH
    # construct a new bytes object
    # add bytes of plat sepolicy.cil file into it
    # read all policy mapping files in the mapping directory, then add the bytes of each one to the bytes object
    # construct a new sha256 object
    # feed it using its update() method
    # then finally return the hex digest retrieved using hexdigest() method
    order3bytes: bytes = bytes()
    platfd = open(PLAT_SEPOLICY_CIL_FILE_ABS_PATH, 'rb')
    order3bytes += platfd.read()
    platfd.close()
    # concatinate the contents of all mapping sepolicy files
    for __mappingfile in listdir(POLICY_MAPPING_DIR_ABS_PATH):
        fd = open(f"{POLICY_MAPPING_DIR_ABS_PATH}\\{__mappingfile}", 'rb')
        order3bytes += fd.read()
        fd.close()
    
    sha256obj: hashlib.sha256 = hashlib.sha256()
    sha256obj.update(order3bytes)
    return sha256obj.hexdigest()


def writeFormattedPlatSepolicyAndMappingHashFile(
    out_filename: str = f"{CURRENT_DIRECTORY}\\plat_sepolicy_and_mapping.sha256",
    ordering: int = 1 # first order: plat sepolicy.cil + latest mapping file
) -> None:
    generated_sha256: str = ""
    if ordering == 1:
        generated_sha256 = generateOrder1HexHash()
    elif ordering == 2:
        generated_sha256 = generateOrder2HexHash()
    elif ordering == 3:
        generated_sha256 = generateOrder3HexHash()
    else: return None

    # write to the desired file
    # open the file using 'w' mode 
    # with LF line endings
    # with two lines: first one desired hex hash, second one is just an empty line
    write_fd = open(out_filename, 'wb') # write using LF line endings
    write_fd.write(bytes(generated_sha256 + '\n', encoding='utf-8'))
    write_fd.close()

    pass

# add CIL Common intermediate language sepolicy fix to the plat_sepolicy.cil
def addCILSEPolicyRule(out_filename: str = f"{CURRENT_DIRECTORY}\\plat_sepolicy.cil") -> None:
    global SEPOLICY_ALLOW_RULE_LINE
    # open file in binary append mode
    # append the new SEPolicy allow rule BUT in binary
    # save changes in binary
    plat_binfd = open(out_filename, 'ba')
    plat_binfd.write(
        bytes('\n' + SEPOLICY_ALLOW_RULE_LINE + '\n', encoding='utf-8')
    )
    plat_binfd.close()

# The Mapping Hunter (as Gemini calls it) function but written by my hands
# This function reads the hex hash inside the current plat_sepolicy_and_mapping.sha256 file
# tries to detect which order it follows:
# First order: Plat sepolicy.cil + Latest mapping file
# Second order: Latest mapping file + Plat sepolicy.cil
# Third order: Plat sepolicy.cil + All mapping files
def checkHexHashFileOrder(
    hex_hash_filename: str = f"{CURRENT_DIRECTORY}\\plat_sepolicy_and_mapping.sha256"
) -> int:
    """
    Checks which order the current plat_sepolicy_and_mapping.sha256 file follows, then
    returns its order number.

    Returns -1 on failure.

    Valid orders are: 1, 2, and 3
    Order 1 : Plat sepolicy.cil + latest mapping file
    Order 2 : Latest mapping file + Plat sepolicy.cil
    Order 3 : Plat sepolicy.cil + all mapping files
    """
    # generate all hex hashes for all 3 orders
    # compare the contents of the first line of the sha256 file with each of these orders
    # then return the matching order
    order1_hexhash: str = generateOrder1HexHash()
    order2_hexhash: str = generateOrder2HexHash()
    order3_hexhash: str = generateOrder3HexHash()
    # get the contents of the first line of the sha256 file
    hexhash_fd = open(hex_hash_filename, mode='r', encoding='utf-8')
    # compare
    fline: str = hexhash_fd.read().split('\n')[0]
    if fline == order1_hexhash: return 1
    elif fline == order2_hexhash: return 2
    elif fline == order3_hexhash: return 3
    else: return -1



def main() -> int:
    # we need to retrieve our current order number
    order_number: int = checkHexHashFileOrder()
    if order_number == 1:
        print("Test 1: Plat Sepolicy.cil + latest mapping file")
    elif order_number == 2:
        print("Test 2: Latest mapping file + Plat sepolicy.cil")
    elif order_number == 3:
        print("Test 3: Plat sepolicy.cil + all mapping files")
    else:
        print("None of the tests match, Please check to see if you have a correct AOSP image!")
        return 255
    
    # add new CIL Sepolicy allow rule
    addCILSEPolicyRule()

    # then write formatted plat sepolicy and mapping sha256 file
    writeFormattedPlatSepolicyAndMappingHashFile(ordering=order_number)

    # check if we wrote it properly!
    assert checkHexHashFileOrder() == order_number

    return 0


def test():
    assert checkHexHashFileOrder() == 1


if __name__ == '__main__':
    __retval: int = main()
    raise SystemExit(__retval)
    # test()
