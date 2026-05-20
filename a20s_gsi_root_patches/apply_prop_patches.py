"""
A Script file to apply build.prop patches for the GSI image to do all of the following:

* Reflect correct device model and manufacturer (samsung, a20sxx, SM-A207F)
* Use vendor fingerprint as system fingerprint (important, fixes bootloops)

This script file is provided as a part of the a14gsifixesa20s module

Copyright (C) 2026 - Ziad (Mr.X)'s Software

"""
# our goals are as follows:
# change ONLY Specified props to the given values
# preserve line endings as LF
from sys import argv as _argv
from os import path


_argc: int = len(_argv)

# constants
CURRENT_DIRECTORY: str = path.dirname(path.abspath(__file__))
# full paths of build.prop files relative to the current working directory (cwd)
BUILD_PROP_FILES: list = [
    "\\system\\build.prop",
    "\\system\\system_dlkm\\etc\\build.prop",
    "\\system\\product\\etc\\build.prop",
    "\\system\\system_ext\\etc\\build.prop"
]


PROPS_NEW_VALUES: dict = {
    "ro.product.system.brand":"samsung",
    "ro.product.system.device":"a20s",
    "ro.product.system.manufacturer":"samsung",
    "ro.product.system.model":"SM-A207F",
    "ro.product.system.name":"a20sxx",
    "ro.build.product":"a20s",
    "vendor.usb.use_ffs_mtp":"0",
    "ro.product.system_dlkm.brand":"samsung",
    "ro.product.system_dlkm.device":"a20s",
    "ro.product.system_dlkm.manufacturer":"samsung",
    "ro.product.system_dlkm.model":"SM-A207F",
    "ro.product.system_dlkm.name":"a20sxx",
    "ro.product.product.brand":"samsung",
    "ro.product.product.device":"a20s",
    "ro.product.product.manufacturer":"samsung",
    "ro.product.product.model":"SM-A207F",
    "ro.product.product.name":"a20sxx",
    "ro.product.system_ext.brand":"samsung",
    "ro.product.system_ext.device":"a20s",
    "ro.product.system_ext.manufacturer":"samsung",
    "ro.product.system_ext.model":"SM-A207F",
    "ro.product.system_ext.name":"a20sxx",
    # build fingerprint
    "ro.system_ext.build.fingerprint":"samsung/a20sxx/a20s:11/RP1A.200720.012/A207FXXS5CWF1:user/release-keys",
    "ro.product.build.fingerprint":"samsung/a20sxx/a20s:11/RP1A.200720.012/A207FXXS5CWF1:user/release-keys",
    "ro.system_dlkm.build.fingerprint":"samsung/a20sxx/a20s:11/RP1A.200720.012/A207FXXS5CWF1:user/release-keys",
    "ro.system.build.fingerprint":"samsung/a20sxx/a20s:11/RP1A.200720.012/A207FXXS5CWF1:user/release-keys"
}


def writePropsToBuildPropFile(fpath: str) -> bool:
    global PROPS_NEW_VALUES
    # open file as read text mode
    try: rfd = open(fpath, mode='r', encoding='utf-8')
    except: return False
    # store all lines of the file as a list
    try: lines: list = rfd.readlines()
    except: return False
    # print debugging
    # print(lines)
    # iterate over each line, see if it exists within our props new value dict
    # if it does, modify the value of the prop inside the lines list
    for i in range(len(lines)):
        # split each line into a key value pair
        try: __prop: list = lines[i].split("=")
        except: continue
        # see if prop name exists in our dict or not.
        if __prop[0] in PROPS_NEW_VALUES:
            # there we go, we just need to write a new value for it.
            lines[i] = f"{__prop[0]}={PROPS_NEW_VALUES[__prop[0]]}\n"
    
    # next up, we have to write it in binary (write mode) to preserve line endings
    # first thing, close the currently open fd
    rfd.close()
    # open the existing file with write binary mode 
    try: wfd = open(fpath, mode='wb')
    except: return False
    # construct a bytes object that contains raw bytes to be written to the file
    data: bytes = bytes()
    # iterate over each line in the lines array
    for line in lines:
        # convert each line to bytes object, then add it into the bytes object
        data += bytes(line, encoding='utf-8')
        
    # next up, we have to write the changes to our file
    wfd.write(data)
    # close file descriptor
    wfd.close()



    return True



def main(argc: int = _argc, argv: list = _argv) -> int:
    # print(CURRENT_DIRECTORY)
    __all_success_flag: bool = True
    print(f"Applying build.prop patches to {len(BUILD_PROP_FILES)} files...")
    for _bpropfilepath in BUILD_PROP_FILES:
        print(f"Patching file \"{CURRENT_DIRECTORY}\\{_bpropfilepath}\"...")
        if not writePropsToBuildPropFile(f"{CURRENT_DIRECTORY}\\{_bpropfilepath}"):
            print(f"ERROR: Failed to apply patch to file: \"{CURRENT_DIRECTORY}\\{_bpropfilepath}\"!")
            __all_success_flag = False
        else:
            print(f"Successfully patched build prop file: \"{CURRENT_DIRECTORY}\\{_bpropfilepath}\"!")
        

    if not __all_success_flag: return 255
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
