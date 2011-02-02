#include <StringUtils.h>

void replace(string& in, const string& search, const string& replace)
{
    string::size_type pos = 0;

    while ((pos = in.find(search, pos)) != string::npos)
    {
        in.replace(pos, search.size(), replace);
        pos++;
    }
}

