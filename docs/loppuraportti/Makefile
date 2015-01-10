all: loppuraportti

loppuraportti: loppuraportti.md pandoctemplate.tex
	pandoc -s --smart -f markdown --template=pandoctemplate.tex --latex-engine=xelatex -o loppuraportti.pdf loppuraportti.md
