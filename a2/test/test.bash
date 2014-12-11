fail=0;
failed=0;
passed=0;

# these files should parse, not necessarily compile.
for f in parse/*.a2; do
	echo -n ../a2 parse $f;
	if (../a2 parse $f 2> /dev/null > /dev/null); then
		(( passed++ ));
		echo " OK";
	else
		fail=1;
		(( failed++ ));
		echo " FAIL";
	fi;
done;

# these files should compile.
for f in *.a2; do
	echo -n ../a2 compile $f;
	if (../a2 compile $f 2> /dev/null > /dev/null); then
		(( passed++ ));
		echo " OK";
	else
		fail=1;
		(( failed++ ));
		echo " FAIL";
	fi;
done;


# these files should fail to compile
for f in bad/*.a2; do
	echo -n ../a2 parse $f;
	if (../a2 compile $f 2> /dev/null > /dev/null); then
		fail=1;
		(( failed++ ));
		echo " FAIL: did not return error";
	else
		(( passed++ ));
		echo " OK";
	fi;
done;



echo $failed failed, $passed passed
exit $fail;
