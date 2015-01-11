all: loppuraportti

loppuraportti: loppuraportti.md pandoctemplate.tex
	pandoc -s --smart -f markdown+raw_tex --template=pandoctemplate.tex --latex-engine=xelatex -o loppuraportti.pdf loppuraportti.md
